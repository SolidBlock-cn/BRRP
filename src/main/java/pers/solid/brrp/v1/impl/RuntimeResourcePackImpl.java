package pers.solid.brrp.v1.impl;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.*;
import pers.solid.brrp.v1.mixin.BuiltinRegistriesAccessor;
import pers.solid.brrp.v1.mixin.RegistryBuilderAccessor;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
  private static final ResourcePackSource RUNTIME = ResourcePackSource.create(name -> Text.translatable("pack.nameAndSource", name, Text.translatable("pack.source.runtime")).formatted(Formatting.GRAY), true);
  public final BlockLootTableGenerator blockLootTableGenerator;
  private final Map<Identifier, Supplier<byte[]>> data = new ConcurrentHashMap<>();
  private final Map<Identifier, Supplier<byte[]>> assets = new ConcurrentHashMap<>();
  private final Map<List<String>, Supplier<byte[]>> root = new ConcurrentHashMap<>();

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated(forRemoval = true)
  private final RegistryWrapper.WrapperLookup registryLookup;
  private static final com.google.common.base.Supplier<RegistryWrapper.@NotNull WrapperLookup> lookupSupplier = () -> {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().getServer() != null) {
      return MinecraftClient.getInstance().getServer().getRegistryManager();
    } else {
      return Holder.registryLookup;
    }
  };
  private static final com.google.common.base.Supplier<@NotNull RegistryOps<JsonElement>> registryOpsSupplier = () -> lookupSupplier.get().getOps(JsonOps.INSTANCE);
  private com.google.common.base.Supplier<RegistryWrapper.@NotNull WrapperLookup> lookupMemorizedSupplier = Suppliers.memoize(lookupSupplier);
  private com.google.common.base.Supplier<@NotNull RegistryOps<JsonElement>> registryOpsMemorizedSupplier = Suppliers.memoize(registryOpsSupplier);

  @Deprecated(since = "1.1.0", forRemoval = true)
  public RuntimeResourcePackImpl(Identifier id, @NotNull RegistryWrapper.WrapperLookup registryLookup) {
    super(id);
    this.registryLookup = registryLookup;
    this.blockLootTableGenerator = BRRPBlockLootTableGenerator.of(registryLookup);
  }

  public RuntimeResourcePackImpl(Identifier id) {
    super(id);
    this.registryLookup = Holder.registryLookup;
    this.blockLootTableGenerator = Holder.blockLootTableGenerator;
  }

  private static Identifier fix(Identifier identifier, String prefix, String append) {
    return identifier.brrp_prefix_and_suffixed(prefix + '/', '.' + append);
  }

  @Override
  public byte[] serialize(Object object) {
    return RuntimeResourcePack.serialize(object, GSON);
  }

  //<editor-fold desc="check duplicate methods">

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

  private void checkDuplicateAsset(Identifier id) {
    if (!allowsDuplicateResource && assets.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate asset id %s in runtime resource pack %s.", id, getDisplayName().getString()));
    }
  }

  private void checkDuplicateData(Identifier id) {
    if (!allowsDuplicateResource && data.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate data id %s in runtime resource pack %s.", id, getDisplayName().getString()));
    }
  }

  private void checkDuplicateResource(ResourceType resourceType, Identifier id) {
    switch (resourceType) {
      case CLIENT_RESOURCES -> checkDuplicateAsset(id);
      case SERVER_DATA -> checkDuplicateData(id);
    }
  }
  //</editor-fold>

  private void checkDuplicateRootResource(String path) {
    if (!allowsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getDisplayName().getString()));
    }
  }

  @Override
  public byte[] addLang(Identifier identifier, byte[] serializedData) {
    return this.addAsset(fix(identifier, "lang", "json"), serializedData);
  }

  @Override
  public byte[] addLang(Identifier identifier, LanguageProvider lang) {
    this.addImmediateAsset(fix(identifier, "lang", "json"), new ImmediateResourceSupplier.OfSimpleResource.Impl<>(LanguageProvider.CODEC, lang));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public byte[] addLootTable(Identifier identifier, byte[] serializedData) {
    return this.addData(fix(identifier, "loot_table", "json"), serializedData);
  }

  @Override
  public byte[] addLootTable(Identifier identifier, LootTable lootTable) {
    this.addImmediateData(fix(identifier, "loot_table", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(LootTable.CODEC, wrapperLookup -> lootTable));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public void addLootTable(Identifier identifier, RegistryResourceFunction<LootTable> lootTable) {
    this.addImmediateData(fix(identifier, "loot_table", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(LootTable.CODEC, lootTable));
  }

  @Override
  public Future<byte[]> addAsyncResource(ResourceType type, Identifier path, FailableFunction<Identifier, byte[], Exception> data) {
    Future<byte[]> future = EXECUTOR_SERVICE.submit(() -> data.apply(path));
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    checkDuplicateResource(type, path);
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
    checkDuplicateResource(type, path);
    sys.put(path, Suppliers.memoize(() -> func.apply(this, path)));
  }

  @Override
  public byte[] addResource(ResourceType type, Identifier path, byte[] data) {
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    checkDuplicateResource(type, path);
    sys.put(path, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public Future<byte[]> addAsyncRootResource(String path, FailableFunction<String, byte[], Exception> data) {
    checkDuplicateRootResource(path);
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
    checkDuplicateRootResource(path);
    this.root.put(Arrays.asList(path.split("/")), Suppliers.memoize(() -> data.apply(this, path)));
  }

  @Override
  public byte[] addRootResource(String path, byte[] data) {
    checkDuplicateRootResource(path);
    this.root.put(Arrays.asList(path.split("/")), () -> data);
    return data;
  }

  @Override
  public <T> void addDirectRootResource(String path, ImmediateResourceSupplier<T> data) {
    checkDuplicateRootResource(path);
    this.root.put(Arrays.asList(path.split("/")), data);
  }

  @Override
  public byte[] addAsset(Identifier id, byte[] data) {
    checkDuplicateAsset(id);
    assets.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public <T> void addImmediateAsset(Identifier id, ImmediateResourceSupplier<T> data) {
    checkDuplicateAsset(id);
    assets.put(id, data);
  }

  @Override
  public byte[] addData(Identifier id, byte[] data) {
    checkDuplicateData(id);
    this.data.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public <T> void addImmediateData(Identifier id, ImmediateResourceSupplier<T> data) {
    checkDuplicateData(id);
    this.data.put(id, data);
  }

  @Override
  public byte[] addBlockState(Identifier id, byte[] serializedData) {
    return addAsset(fix(id, "blockstates", "json"), serializedData);
  }

  @Override
  public byte[] addBlockState(Identifier id, @NotNull BlockStateSupplier state) {
    final JsonElement jsonTree = state.get();
    addImmediateAsset(fix(id, "blockstates", "json"), new ImmediateResourceSupplier.OfJson.Impl(jsonTree));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public byte[] addModel(Identifier id, byte[] serializedData) {
    return addAsset(fix(id, "models", "json"), serializedData);
  }

  @Override
  public byte[] addModel(Identifier id, ModelJsonBuilder model) {
    final JsonElement jsonTree = GSON.toJsonTree(model);
    addImmediateAsset(fix(id, "models", "json"), new ImmediateResourceSupplier.OfJson.Impl(jsonTree));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
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
    final Identifier id = Identifier.of(tagKey.id().getNamespace(), RegistryKeys.getTagPath(tagKey.registry()) + "/" + tagKey.id().getPath() + ".json");
    addImmediateData(id, new ImmediateResourceSupplier.OfSimpleResource.Impl<>(TagFile.CODEC, new TagFile(tagBuilder.build(), false)));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public byte[] addAnimation(Identifier id, byte[] serializedData) {
    return this.addAsset(fix(id, "textures", "png.mcmeta"), serializedData);
  }

  @Override
  public byte[] addRecipe(Identifier id, byte[] serializedData) {
    return this.addData(fix(id, "recipe", "json"), serializedData);
  }

  @Override
  public byte[] addRecipe(Identifier id, Recipe<?> recipe) {
    this.addImmediateData(fix(id, "recipe", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(Recipe.CODEC, r -> recipe));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public void addRecipe(Identifier id, RegistryResourceFunction<Recipe<?>> recipe) {
    this.addImmediateData(fix(id, "recipe", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(Recipe.CODEC, recipe));
  }

  @Override
  public byte[] addAdvancement(Identifier id, byte[] serializedData) {
    return this.addData(fix(id, "advancement", "json"), serializedData);
  }

  @Override
  public byte[] addAdvancement(Identifier id, Advancement advancement) {
    this.addImmediateData(fix(id, "advancement", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(Advancement.CODEC, r -> advancement));
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  @Override
  public void addAdvancement(Identifier id, RegistryResourceFunction<Advancement> advancement) {
    this.addImmediateData(fix(id, "advancement", "json"), new ImmediateResourceSupplier.OfRegistryResource.Impl<>(Advancement.CODEC, advancement));
  }

  @Override
  public Future<?> async(Consumer<RuntimeResourcePack> action) {
    return EXECUTOR_SERVICE.submit(() -> action.accept(this));
  }

  @Override
  public void dumpInPath(Path output, @Nullable ResourceType dumpResourceType, int @Nullable [] stat) {
    LOGGER.info("Dumping {} in the path {}. The path will be cleared.", getDisplayName().getString(), output);

    this.lookupMemorizedSupplier = Suppliers.memoize(lookupSupplier);
    this.registryOpsMemorizedSupplier = Suppliers.memoize(registryOpsSupplier);

    try {
      if (stat != null) stat[0] = -1;
      if (output.toFile().exists()) {
        FileUtils.deleteDirectory(output.toFile());
      }
      Files.createDirectories(output);
      if (stat != null) {
        stat[0] = stat[1] = stat[2] = 0;
      }
      if (!root.isEmpty()) {
        for (Map.Entry<List<String>, Supplier<byte[]>> e : this.root.entrySet()) {
          Path rootPath = output.resolve(String.join("/", e.getKey()));
          this.writeAtPath(e.getValue(), rootPath);
          if (stat != null) stat[0] += 1;
          if (Thread.interrupted()) {
            throw new InterruptedException("Dumping root resources");
          }
        }
      }

      if (!root.containsKey(List.of("pack.mcmeta"))) {
        Path rootPath = output.resolve("pack.mcmeta");
        this.writeAtPath(new ImmediateResourceSupplier.OfJson.Impl(createMetadataJson()), rootPath);
        if (stat != null) stat[0] += 1;
      }

      if (dumpResourceType != ResourceType.SERVER_DATA && !assets.isEmpty()) {
        Path assetsPath = output.resolve("assets");
        Files.createDirectories(assetsPath);
        for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.assets.entrySet()) {
          this.write(assetsPath, entry.getKey(), entry.getValue());
          if (stat != null) stat[1] += 1;
          if (Thread.interrupted()) throw new InterruptedException("Dumping server data");
        }
      }
      if (dumpResourceType != ResourceType.CLIENT_RESOURCES && !data.isEmpty()) {
        Path dataPath = output.resolve("data");
        Files.createDirectories(dataPath);
        for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.data.entrySet()) {
          this.write(dataPath, entry.getKey(), entry.getValue());
          if (stat != null) stat[2] += 1;
          if (Thread.interrupted()) throw new InterruptedException("Dumping client resources");
        }
      }
      LOGGER.info("Dumping {} finished.", getDisplayName().getString());
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
  public void dump(ZipOutputStream zos, @Nullable ResourceType dumpResourceType, int[] stat) throws IOException {
    this.lookupMemorizedSupplier = Suppliers.memoize(lookupSupplier);
    this.registryOpsMemorizedSupplier = Suppliers.memoize(registryOpsSupplier);

    for (Map.Entry<List<String>, Supplier<byte[]>> entry : this.root.entrySet()) {
      zos.putNextEntry(new ZipEntry(String.join("/", entry.getKey())));
      this.writeToStream(entry.getValue(), zos);
      if (stat != null) stat[0] += 1;
    }
    if (!root.containsKey(List.of("pack.mcmeta"))) {
      zos.putNextEntry(new ZipEntry("pack.mcmeta"));
      this.writeToStream(new ImmediateResourceSupplier.OfJson.Impl(createMetadataJson()), zos);
      if (stat != null) stat[0] += 1;
    }

    if (dumpResourceType != ResourceType.SERVER_DATA && !assets.isEmpty()) {
      for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.assets.entrySet()) {
        Identifier id = entry.getKey();
        zos.putNextEntry(new ZipEntry("assets/" + id.getNamespace() + "/" + id.getPath()));
        this.writeToStream(entry.getValue(), zos);
        if (stat != null) stat[1] += 1;
      }
    }

    if (dumpResourceType != ResourceType.CLIENT_RESOURCES && !data.isEmpty()) {
      for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.data.entrySet()) {
        Identifier id = entry.getKey();
        zos.putNextEntry(new ZipEntry("data/" + id.getNamespace() + "/" + id.getPath()));
        this.writeToStream(entry.getValue(), zos);
        if (stat != null) stat[2] += 1;
      }
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
    if (supplier instanceof ImmediateResourceSupplier<?> immediateResourceSupplier) {
      return immediateResourceSupplier.getImmediateInputSupplier();
    }
    if (supplier == null) {
      return null;
    }
    return () -> new ByteArrayInputStream(supplier.get());
  }

  @Nullable
  @Override
  public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
    Supplier<byte[]> supplier = this.getSys(type).get(id);
    if (supplier instanceof ImmediateResourceSupplier<?> immediateResourceSupplier) {
      return immediateResourceSupplier.getImmediateInputSupplier();
    }

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
      if (supplier instanceof ImmediateInputSupplier.OfJson ofJson) {
        try {
          final JsonElement jsonElement = ofJson.jsonElement();
          if (jsonElement != null) {
            return metaReader.fromJson(JsonHelper.getObject(jsonElement.getAsJsonObject(), metaReader.getKey()));
          }
        } catch (Exception e) {
          LOGGER.error("Couldn't load immediate metadata {} for runtime resource pack {}", metaReader.getKey(), getId(), e);
        }
      }
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
        return metaReader.fromJson(createMetadataJson().getAsJsonObject("pack"));
      }
      return null;
    }
  }

  @Override
  public ResourcePackInfo getInfo() {
    return new ResourcePackInfo(getId(), getDisplayName(), RUNTIME, Optional.empty());
  }

  @Override
  public void close() {
    LOGGER.debug("Closing Runtime Resource Pack {}.", getDisplayName().getString());
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
    map.put(Identifier.of(namespace, path), () -> data);
  }

  private void write(Path dir, Identifier identifier, Supplier<byte[]> dataSupplier) throws IOException {
    Path file = dir.resolve(identifier.getNamespace()).resolve(identifier.getPath());
    writeAtPath(dataSupplier, file);
  }

  private void writeAtPath(Supplier<byte[]> dataSupplier, Path path) throws IOException {
    Files.createDirectories(path.getParent());
    try (OutputStream outputStream = Files.newOutputStream(path)) {
      writeToStream(dataSupplier, outputStream);
    }
  }

  @ApiStatus.AvailableSince("1.1.0")
  private void writeToStream(Supplier<byte[]> dataSupplier, OutputStream outputStream) throws IOException {
    if (dataSupplier instanceof final ImmediateResourceSupplier<?> ir) {
      // when calling this method,
      // you must ensure the Supplier objects are not empty.
      final JsonElement element;
      switch (ir) {
        case ImmediateResourceSupplier.OfRegistryResource<?> ofRegistryResource -> {
          final RegistryWrapper.WrapperLookup lookup = lookupMemorizedSupplier.get();
          final RegistryOps<JsonElement> ops = registryOpsMemorizedSupplier.get();
          element = ofRegistryResource.getJsonElement(ops, lookup);
        }
        case ImmediateResourceSupplier.OfJson ofJson -> element = ofJson.jsonElement();
        case ImmediateResourceSupplier.OfSimpleResource<?> ofSimpleResource -> {
          final RegistryOps<JsonElement> ops = registryOpsMemorizedSupplier.get();
          element = ofSimpleResource.getJsonElement(ops);
        }
        default -> element = null;
      }
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
      try {
        if (element != null) {
          GSON.toJson(element, writer);
        } else {
          writer.write("<resource not supported>");
        }
      } finally {
        writer.flush();
      }
    } else if (dataSupplier instanceof final ImmediateResourceSupplier.OfJson ofJson) {
      final JsonElement element = ofJson.jsonElement().deepCopy();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
      try {
        GSON.toJson(element, writer);
      } finally {
        writer.flush();
      }
    } else {
      outputStream.write(dataSupplier.get());
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

  @SuppressWarnings("removal")
  @Deprecated(since = "1.1.0", forRemoval = true)
  @Override
  public RegistryWrapper.WrapperLookup getRegistryLookup() {
    return registryLookup;
  }

  @SuppressWarnings("removal")
  @Deprecated(since = "1.1.0", forRemoval = true)
  @Override
  public BlockLootTableGenerator getBlockLootTableGenerator() {
    return blockLootTableGenerator;
  }

  protected Map<Identifier, Supplier<byte[]>> getSys(ResourceType side) {
    return side == ResourceType.CLIENT_RESOURCES ? this.assets : this.data;
  }

  public static class Workaround extends RegistryBuilder {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Contract(pure = true)
    public static Workaround copy(RegistryBuilder from) {
      final Workaround rb = new Workaround();
      final List vanilla = ((RegistryBuilderAccessor) from).getRegistries();
      ((RegistryBuilderAccessor) rb).getRegistries().addAll(vanilla);
      return rb;
    }
  }

  /**
   * This class holds some shared {@code registryLookup} and {@code gson} that many runtime resource packs may use in common.
   */
  private static final class Holder {
    private static final RegistryWrapper.WrapperLookup registryLookup = Util.make(() -> {
      final RegistryBuilder rb = Workaround.copy(BuiltinRegistriesAccessor.getRegistryBuilder());
      return rb.createWrapperLookup(DynamicRegistryManager.of(Registries.REGISTRIES));
    });

    private static final BlockLootTableGenerator blockLootTableGenerator = new BRRPBlockLootTableGenerator(registryLookup);
  }
}