package pers.solid.brrp.v1.impl;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * @see RuntimeResourcePack
 */
@ApiStatus.Internal
public class RuntimeResourcePackImpl extends AbstractRuntimeResourcePack implements ResourcePack {
  public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("BRRP-Workers-%s").build());

  private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeResourcePackImpl.class);
  private final Map<Identifier, Supplier<byte[]>> data = new ConcurrentHashMap<>();
  private final Map<Identifier, Supplier<byte[]>> assets = new ConcurrentHashMap<>();
  private final Map<List<String>, Supplier<byte[]>> root = new ConcurrentHashMap<>();

  public RuntimeResourcePackImpl(Identifier id) {
    super(id);
  }


  private static Identifier fix(Identifier identifier, String prefix, String append) {
    return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath() + '.' + append);
  }

  @Override
  public void addRecoloredImage(Identifier identifier, InputStream target, IntUnaryOperator operator) {
    this.addLazyResource(ResourceType.CLIENT_RESOURCES, fix(identifier, "textures", "png"), (i, r) -> {
      try {
        // optimize buffer allocation, input and output image after recoloring should be roughly the same size
        CountingInputStream is = new CountingInputStream(target);
        // repaint image
        BufferedImage base = ImageIO.read(is);
        BufferedImage recolored = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < base.getHeight(); y++) {
          for (int x = 0; x < base.getWidth(); x++) {
            recolored.setRGB(x, y, operator.applyAsInt(base.getRGB(x, y)));
          }
        }
        // write image
        ByteArrayOutputStream stream = new ByteArrayOutputStream(is.getCount());
        ImageIO.write(recolored, "png", stream);
        return stream.toByteArray();
      } catch (Throwable e) {
        LOGGER.error("Failed to add resources:", e);
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public byte[] addLang(Identifier identifier, byte[] serializedData) {
    return this.addAsset(fix(identifier, "lang", "json"), serializedData);
  }

  @Override
  public byte[] addLootTable(Identifier identifier, byte[] serializedData) {
    return this.addData(fix(identifier, "loot_tables", "json"), serializedData);
  }

  @Override
  public Future<byte[]> addAsyncResource(ResourceType type, Identifier path, FailableFunction<Identifier, byte[], Exception> data) {
    Future<byte[]> future = EXECUTOR_SERVICE.submit(() -> data.apply(path));
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    if (!allowsDuplicateResource && sys.containsKey(path)) {
      throw new IllegalArgumentException(String.format("Duplicate resource id %s in runtime resource pack %s.", path, getName()));
    }
    sys.put(path, () -> {
      try {
        return future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
    return future;
  }

  @Override
  public void addLazyResource(ResourceType type, Identifier path, BiFunction<RuntimeResourcePack, Identifier, byte[]> func) {
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    if (!allowsDuplicateResource && sys.containsKey(path)) {
      throw new IllegalArgumentException(String.format("Duplicate resource id %s in runtime resource pack %s.", path, getName()));
    }
    sys.put(path, Suppliers.memoize(() -> func.apply(this, path)));
  }

  @Override
  public byte[] addResource(ResourceType type, Identifier path, byte[] data) {
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    if (!allowsDuplicateResource && sys.containsKey(path)) {
      throw new IllegalArgumentException(String.format("Duplicate resource id %s in runtime resource pack %s.", path, getName()));
    }
    sys.put(path, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public Future<byte[]> addAsyncRootResource(String path, FailableFunction<String, byte[], Exception> data) {
    if (!allowsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    Future<byte[]> future = EXECUTOR_SERVICE.submit(() -> data.apply(path));
    this.root.put(Arrays.asList(path.split("/")), () -> {
      try {
        return future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
    return future;
  }

  @Override
  public void addLazyRootResource(String path, BiFunction<RuntimeResourcePack, String, byte[]> data) {
    if (!allowsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    this.root.put(Arrays.asList(path.split("/")), Suppliers.memoize(() -> data.apply(this, path)));
  }

  @Override
  public byte[] addRootResource(String path, byte[] data) {
    if (!allowsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    this.root.put(Arrays.asList(path.split("/")), () -> data);
    return data;
  }

  @Override
  public byte[] addAsset(Identifier id, byte[] data) {
    if (!allowsDuplicateResource && assets.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate asset id %s in runtime resource pack %s!", id, getName()));
    }
    assets.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public byte[] addData(Identifier id, byte[] data) {
    if (!allowsDuplicateResource && this.data.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate data id %s in runtime resource pack %s!", id, getName()));
    }
    this.data.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public byte[] addBlockState(Identifier id, byte[] serializedData) {
    return addAsset(fix(id, "blockstates", "json"), serializedData);
  }

  @Override
  public byte[] addModel(Identifier id, byte[] serializedData) {
    return addAsset(fix(id, "models", "json"), serializedData);
  }

  @Override
  public byte[] addTexture(Identifier id, BufferedImage image) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "png", stream);
    } catch (IOException e) {
      throw new RuntimeException("impossible.", e);
    }
    return this.addAsset(fix(id, "textures", "png"), stream.toByteArray());
  }

  @Override
  public byte[] addTag(Identifier fullId, byte[] serializedData) {
    return this.addData(fix(fullId, "tags", "json"), serializedData);
  }

  @Override
  public <T> byte[] addTag(TagKey<T> tagKey, TagBuilder tagBuilder) {
    return addData(new Identifier(tagKey.id().getNamespace(), TagManagerLoader.getPath(tagKey.registry()) + "/" + tagKey.id().getPath() + ".json"), serialize(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false)).getOrThrow(false, LOGGER::error)));
  }

  @Override
  public byte[] addAnimation(Identifier id, byte[] serializedData) {
    return this.addAsset(fix(id, "textures", "png.mcmeta"), serializedData);
  }

  @Override
  public byte[] addRecipe(Identifier id, byte[] serializedData) {
    return this.addData(fix(id, "recipes", "json"), serializedData);
  }

  @Override
  public byte[] addAdvancement(Identifier id, byte[] serializedData) {
    return this.addData(fix(id, "advancements", "json"), serializedData);
  }

  @Override
  public Future<?> async(Consumer<RuntimeResourcePack> action) {
    return EXECUTOR_SERVICE.submit(() -> action.accept(this));
  }

  @Override
  public void dumpInPath(Path output, @Nullable ResourceType dumpResourceType, int @Nullable [] stat) {
    LOGGER.info("Dumping {} in the path {}. The path will be cleared.", getName(), output);
    try {
      if (stat != null) stat[0] = -1;
      if (output.toFile().exists()) {
        FileUtils.cleanDirectory(output.toFile());
      } else {
        Files.createDirectories(output);
      }
      if (stat != null) {
        stat[0] = stat[1] = stat[2] = 0;
      }
      if (!root.isEmpty()) {
        for (Map.Entry<List<String>, Supplier<byte[]>> e : this.root.entrySet()) {
          Path root = output.resolve(String.join("/", e.getKey()));
          Files.createDirectories(root.getParent());
          Files.write(root, e.getValue().get());
          if (stat != null) stat[0] += 1;
          if (Thread.interrupted()) {
            throw new InterruptedException("Dumping root resources");
          }
        }
      }

      if (dumpResourceType != ResourceType.SERVER_DATA && !assets.isEmpty()) {
        Path assets = output.resolve("assets");
        Files.createDirectories(assets);
        for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.assets.entrySet()) {
          this.write(assets, entry.getKey(), entry.getValue().get());
          if (stat != null) stat[1] += 1;
          if (Thread.interrupted()) throw new InterruptedException("Dumping server data");
        }
      }
      if (dumpResourceType != ResourceType.CLIENT_RESOURCES && !data.isEmpty()) {
        Path data = output.resolve("data");
        Files.createDirectories(data);
        for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.data.entrySet()) {
          this.write(data, entry.getKey(), entry.getValue().get());
          if (stat != null) stat[2] += 1;
          if (Thread.interrupted()) throw new InterruptedException("Dumping client resources");
        }
      }
      LOGGER.info("Dumping {} finished.", getName());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    } catch (InterruptedException e) {
      LOGGER.warn("Interrupted when dumping:", e);
    }
  }

  @Override
  public void load(Path dir) throws IOException {
    try (Stream<Path> stream = Files.walk(dir)) {
      for (Path file : (Iterable<Path>) () -> stream.filter(Files::isRegularFile).map(dir::relativize).iterator()) {
        String s = file.toString();
        if (s.startsWith("assets")) {
          String path = s.substring("assets".length() + 1);
          this.load(path, this.assets, Files.readAllBytes(file));
        } else if (s.startsWith("data")) {
          String path = s.substring("data".length() + 1);
          this.load(path, this.data, Files.readAllBytes(file));
        } else {
          byte[] data = Files.readAllBytes(file);
          this.root.put(Arrays.asList(s.split("/")), () -> data);
        }
      }
    }
  }

  @Override
  public void dump(ZipOutputStream zos) throws IOException {
    for (Map.Entry<List<String>, Supplier<byte[]>> entry : this.root.entrySet()) {
      zos.putNextEntry(new ZipEntry(String.join("/", entry.getKey())));
      zos.write(entry.getValue().get());
      zos.closeEntry();
    }

    for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.assets.entrySet()) {
      Identifier id = entry.getKey();
      zos.putNextEntry(new ZipEntry("assets/" + id.getNamespace() + "/" + id.getPath()));
      zos.write(entry.getValue().get());
      zos.closeEntry();
    }

    for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.data.entrySet()) {
      Identifier id = entry.getKey();
      zos.putNextEntry(new ZipEntry("data/" + id.getNamespace() + "/" + id.getPath()));
      zos.write(entry.getValue().get());
      zos.closeEntry();
    }
  }

  @Override
  public void load(ZipInputStream stream) throws IOException {
    ZipEntry entry;
    while ((entry = stream.getNextEntry()) != null) {
      String s = entry.toString();
      if (s.startsWith("assets")) {
        String path = s.substring("assets".length() + 1);
        this.load(path, this.assets, this.read(entry, stream));
      } else if (s.startsWith("data")) {
        String path = s.substring("data".length() + 1);
        this.load(path, this.data, this.read(entry, stream));
      } else {
        byte[] data = this.read(entry, stream);
        this.root.put(Arrays.asList(s.split("/")), () -> data);
      }
    }
  }

  /**
   * pack.png and that's about it, I think/hope
   *
   * @param segments the segments of the file
   * @return the pack.png image as a stream
   */
  @Override
  public InputSupplier<InputStream> openRoot(String... segments) {
    Supplier<byte[]> supplier = this.root.get(Arrays.asList(segments));
    if (supplier == null) {
      return null;
    }
    return () -> new ByteArrayInputStream(supplier.get());
  }

  @Nullable
  @Override
  public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
    Supplier<byte[]> supplier = this.getSys(type).get(id);
    return supplier == null ? null : () -> new ByteArrayInputStream(supplier.get());
  }

  @Override
  public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
    for (Identifier identifier : this.getSys(type).keySet()) {
      // deleted section: detecting "No resource found for..."
      if (identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(prefix)) {
        consumer.accept(identifier, open(type, identifier));
      }
    }
  }

  @Override
  public Set<String> getNamespaces(ResourceType type) {
    Set<String> namespaces = new HashSet<>();
    for (Identifier identifier : this.getSys(type).keySet()) {
      namespaces.add(identifier.getNamespace());
    }
    return namespaces;
  }

  /**
   * modified according to ARRP
   *
   * @author Devan Kerman。
   */
  @Override
  public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
    InputStream stream = null;
    try {
      InputSupplier<InputStream> supplier = this.openRoot("pack.mcmeta");
      if (supplier != null) {
        stream = supplier.get();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (stream != null) {
      return AbstractFileResourcePack.parseMetadata(metaReader, stream);
    } else {
      if (metaReader.getKey().equals("pack")) {
        JsonObject object = new JsonObject();
        object.addProperty("pack_format", this.packVersion);
        final Text description = getDescription();
        if (description != null) {
          object.add("description", Text.Serializer.toJsonTree(description));
        } else {
          object.add("description", Text.Serializer.toJsonTree(Text.translatable("brrp.pack.defaultDescription", getId())));
        }
        return metaReader.fromJson(object);
      }
      return null;
    }
  }

  @Override
  public String getName() {
    return "Runtime Resource Pack " + this.getId().toString();
  }

  @Override
  public void close() {
    LOGGER.debug("Closing {}.", getName());
  }

  protected byte[] read(ZipEntry entry, InputStream stream) throws IOException {
    byte[] data = new byte[Math.toIntExact(entry.getSize())];
    if (stream.read(data) != data.length) {
      throw new IOException("Zip stream was cut off! (maybe incorrect zip entry length? maybe u didn't flush your stream?)");
    }
    return data;
  }

  protected void load(String fullPath, Map<Identifier, Supplier<byte[]>> map, byte[] data) {
    int sep = fullPath.indexOf('/');
    String namespace = fullPath.substring(0, sep);
    String path = fullPath.substring(sep + 1);
    map.put(new Identifier(namespace, path), () -> data);
  }

  private void write(Path dir, Identifier identifier, byte[] data) {
    try {
      Path file = dir.resolve(identifier.getNamespace()).resolve(identifier.getPath());
      Files.createDirectories(file.getParent());
      try (OutputStream output = Files.newOutputStream(file)) {
        output.write(data);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void clearResources(ResourceType side) {
    getSys(side).clear();
  }

  @Override
  public void clearResources() {
    assets.clear();
    data.clear();
    root.clear();
  }

  @Override
  public void clearRootResources() {
    root.clear();
  }

  @Override
  public int numberOfClientResources() {
    return assets.size();
  }

  @Override
  public int numberOfServerData() {
    return data.size();
  }

  @Override
  public int numberOfRootResources() {
    return root.size();
  }


  protected Map<Identifier, Supplier<byte[]>> getSys(ResourceType side) {
    return side == ResourceType.CLIENT_RESOURCES ? this.assets : this.data;
  }
}