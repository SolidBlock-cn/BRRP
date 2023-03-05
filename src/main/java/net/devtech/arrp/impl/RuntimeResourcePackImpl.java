package net.devtech.arrp.impl;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.*;
import com.mojang.bridge.game.PackType;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.animation.JAnimation;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.tags.JTag;
import net.devtech.arrp.util.CallableFunction;
import net.devtech.arrp.util.CountingInputStream;
import net.devtech.arrp.util.UnsafeByteArrayOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.PlatformBridge;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.lang.String.valueOf;


/**
 * @see RuntimeResourcePack
 */
@ApiStatus.Internal
public class RuntimeResourcePackImpl implements RuntimeResourcePack, ResourcePack {
  public static final ExecutorService EXECUTOR_SERVICE;
  /**
   * Whether to dump all resources as local files. It depends on the config file. By default, it is {@code false}.
   */
  public static final boolean DUMP;
  /**
   * Whether to print milliseconds used for data generation. By default, it is {@code false}.
   */
  public static final boolean DEBUG_PERFORMANCE;

  public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .registerTypeHierarchyAdapter(JsonSerializable.class, JsonSerializable.SERIALIZER)
      .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer())
      .registerTypeHierarchyAdapter(LootNumberProvider.class, LootNumberProviderTypes.createGsonSerializer())
      .registerTypeHierarchyAdapter(LootTable.class, new LootTable.Serializer())
      .registerTypeHierarchyAdapter(BlockStateSupplier.class, (JsonSerializer<BlockStateSupplier>) (builder, type, jsonSerializationContext) -> builder.get())
      .registerTypeHierarchyAdapter(RecipeJsonProvider.class, ((JsonSerializer<RecipeJsonProvider>) (recipe, typeOrSrc, context) -> recipe.toJson()))
      .registerTypeHierarchyAdapter(Advancement.Builder.class, (JsonSerializer<Advancement.Builder>) (builder, type, jsonSerializationContext) -> builder.toJson())
      .registerTypeAdapter(RecipeCategory.class, (JsonSerializer<RecipeCategory>) (src, type, context) -> new JsonPrimitive(src.getName()))
      .registerTypeHierarchyAdapter(StringIdentifiable.class, (JsonSerializer<StringIdentifiable>) (src, typeOfSrc, context) -> new JsonPrimitive(src.asString()))
      .create();
  // if it works, don't touch it
  /**
   * @since BRRP 0.7.0, ARRP 0.6.2
   * Author: Devan-Kerman
   */
  @ApiStatus.AvailableSince("0.7.0")
  static final Set<String> KEY_WARNINGS = Collections.newSetFromMap(new ConcurrentHashMap<>());
  // @formatter:on
  private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeResourcePackImpl.class);

  static {
    Properties properties = new Properties();
    // Number of threads of the executor service. By default, depends on the config of the device. It is used for async resources, and async data generation defined in `rrp:pregen` entrypoint.
    int processors = Math.max(Runtime.getRuntime().availableProcessors() / 2 - 1, 1);
    // Whether to dump all the resources as local files. By default, false.
    boolean dump = false;
    // Whether to print a notice of milliseconds used for data generation.
    boolean performance = false;
    properties.setProperty("threads", valueOf(processors));
    properties.setProperty("dump assets", "false");
    properties.setProperty("debug performance", "false");

    File file = PlatformBridge.getInstance().getConfigDir().resolve("rrp.properties").toFile();
    try (FileReader reader = new FileReader(file)) {
      properties.load(reader);
      processors = Integer.parseInt(properties.getProperty("threads"));
      dump = Boolean.parseBoolean(properties.getProperty("dump assets"));
      performance = Boolean.parseBoolean(properties.getProperty("debug performance"));
    } catch (Throwable t) {
      LOGGER.warn("Invalid config, creating new one!");
      //noinspection ResultOfMethodCallIgnored
      file.getParentFile().mkdirs();
      try (FileWriter writer = new FileWriter(file)) {
        properties.store(writer, "number of threads RRP should use for generating resources");
      } catch (IOException ex) {
        LOGGER.error("Unable to write to RRP config!", ex);
      }
    }
    EXECUTOR_SERVICE = Executors.newFixedThreadPool(processors, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("BRRP-Workers-%s").build());
    DUMP = dump;
    DEBUG_PERFORMANCE = performance;
    KEY_WARNINGS.add("filter");
    KEY_WARNINGS.add("language");
  }

  public final int packVersion;
  private final Identifier id;
  private final Lock waiting = new ReentrantLock();
  private final Map<Identifier, Supplier<byte[]>> data = new ConcurrentHashMap<>();
  private final Map<Identifier, Supplier<byte[]>> assets = new ConcurrentHashMap<>();
  private final Map<Identifier, JLang> langMergeable = new ConcurrentHashMap<>();
  private final Map<List<String>, Supplier<byte[]>> root = new ConcurrentHashMap<>();
  private boolean forbidsDuplicateResource = false;

  public RuntimeResourcePackImpl(Identifier id) {
    this(id, SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE));
  }

  public RuntimeResourcePackImpl(Identifier id, int version) {
    this.packVersion = version;
    this.id = id;
  }

  private static byte[] serialize(Object object) {
    UnsafeByteArrayOutputStream ubaos = new UnsafeByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(ubaos, StandardCharsets.UTF_8);
    GSON.toJson(object, writer);
    try {
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ubaos.getBytes();
  }

  private static Identifier fix(Identifier identifier, String prefix, String append) {
    return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath() + '.' + append);
  }

  @Override
  public void setForbidsDuplicateResource(boolean b) {
    forbidsDuplicateResource = true;
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
        UnsafeByteArrayOutputStream baos = new UnsafeByteArrayOutputStream(is.bytes());
        ImageIO.write(recolored, "png", baos);
        return baos.getBytes();
      } catch (Throwable e) {
        LOGGER.error("Failed to add resources:", e);
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public byte[] addLang(Identifier identifier, JLang lang) {
    return this.addAsset(fix(identifier, "lang", "json"), serialize(lang));
  }

  @Override
  public void mergeLang(Identifier identifier, JLang lang) {
    this.langMergeable.compute(identifier, (identifier1, existingLang) -> {
      final JLang newLang;
      if (existingLang == null) {
        newLang = new JLang();
        final Identifier path = fix(identifier, "lang", "json");
        assets.put(path, new Memoized<>((pack, identifier2) -> serialize(newLang), path));
      } else {
        newLang = existingLang;
      }
      newLang.putAll(lang);
      return newLang;
    });
  }

  @Override
  public byte[] addLootTable(Identifier identifier, JLootTable table) {
    return this.addData(fix(identifier, "loot_tables", "json"), serialize(table));
  }

  @ApiStatus.AvailableSince("0.8.0")
  @Override
  public byte[] addLootTable(Identifier identifier, LootTable lootTable) {
    return this.addData(fix(identifier, "loot_tables", "json"), serialize(lootTable));
  }

  @Override
  public Future<byte[]> addAsyncResource(ResourceType type, Identifier path, CallableFunction<Identifier, byte[]> data) {
    Future<byte[]> future = EXECUTOR_SERVICE.submit(() -> data.get(path));
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    if (forbidsDuplicateResource && sys.containsKey(path)) {
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
    if (forbidsDuplicateResource && sys.containsKey(path)) {
      throw new IllegalArgumentException(String.format("Duplicate resource id %s in runtime resource pack %s.", path, getName()));
    }
    sys.put(path, new Memoized<>(func, path));
  }

  @Override
  public byte[] addResource(ResourceType type, Identifier path, byte[] data) {
    final Map<Identifier, Supplier<byte[]>> sys = this.getSys(type);
    if (forbidsDuplicateResource && sys.containsKey(path)) {
      throw new IllegalArgumentException(String.format("Duplicate resource id %s in runtime resource pack %s.", path, getName()));
    }
    sys.put(path, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public Future<byte[]> addAsyncRootResource(String path, CallableFunction<String, byte[]> data) {
    if (forbidsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    Future<byte[]> future = EXECUTOR_SERVICE.submit(() -> data.get(path));
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
    if (forbidsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    this.root.put(Arrays.asList(path.split("/")), new Memoized<>(data, path));
  }

  @Override
  public byte[] addRootResource(String path, byte[] data) {
    if (forbidsDuplicateResource && root.containsKey(Arrays.asList(path.split("/")))) {
      throw new IllegalArgumentException(String.format("Duplicate root resource id %s in runtime resource pack %s!", path, getName()));
    }
    this.root.put(Arrays.asList(path.split("/")), () -> data);
    return data;
  }

  @Override
  public byte[] addAsset(Identifier id, byte[] data) {
    if (forbidsDuplicateResource && assets.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate asset id %s in runtime resource pack %s!", id, getName()));
    }
    assets.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public byte[] addData(Identifier id, byte[] data) {
    if (forbidsDuplicateResource && this.data.containsKey(id)) {
      throw new IllegalArgumentException(String.format("Duplicate data id %s in runtime resource pack %s!", id, getName()));
    }
    this.data.put(id, Suppliers.ofInstance(data));
    return data;
  }

  @Override
  public byte[] addModel(JModel model, Identifier id) {
    return this.addAsset(fix(id, "models", "json"), serialize(model));
  }


  @Override
  public byte[] addBlockState(JBlockStates state, Identifier id) {
    return this.addAsset(fix(id, "blockstates", "json"), serialize(state));
  }

  @ApiStatus.AvailableSince("0.8.0")
  @Override
  public byte[] addBlockState(BlockStateSupplier state, Identifier id) {
    return this.addAsset(fix(id, "blockstates", "json"), serialize(state));
  }

  @Override
  public byte[] addTexture(Identifier id, BufferedImage image) {
    UnsafeByteArrayOutputStream ubaos = new UnsafeByteArrayOutputStream();
    try {
      ImageIO.write(image, "png", ubaos);
    } catch (IOException e) {
      throw new RuntimeException("impossible.", e);
    }
    return this.addAsset(fix(id, "textures", "png"), ubaos.getBytes());
  }

  @Override
  public byte[] addAnimation(Identifier id, JAnimation animation) {
    return this.addAsset(fix(id, "textures", "png.mcmeta"), serialize(animation));
  }

  @Override
  public byte[] addTag(Identifier id, JTag tag) {
    return this.addData(fix(id, "tags", "json"), serialize(tag));
  }

  @Override
  public byte[] addRecipe(Identifier id, JRecipe recipe) {
    return this.addData(fix(id, "recipes", "json"), serialize(recipe));
  }

  @ApiStatus.AvailableSince("0.8.0")
  @Override
  public byte[] addRecipe(Identifier id, RecipeJsonProvider recipe) {
    return this.addData(fix(id, "recipes", "json"), serialize(recipe));
  }

  @Override
  public byte[] addAdvancement(Identifier id, Advancement.Builder advancement) {
    return this.addData(fix(id, "advancements", "json"), serialize(advancement));
  }

  @Override
  public Future<?> async(Consumer<RuntimeResourcePack> action) {
    this.lock();
    return EXECUTOR_SERVICE.submit(() -> {
      action.accept(this);
      this.waiting.unlock();
    });
  }

  @Override
  public void dumpDirect(Path output) {
    LOGGER.info("Dumping {} in the path {}.", getName(), output);
    // data dump time
    try {
      for (Map.Entry<List<String>, Supplier<byte[]>> e : this.root.entrySet()) {
        Path root = output.resolve(String.join("/", e.getKey()));
        Files.createDirectories(root.getParent());
        Files.write(root, e.getValue().get());
      }

      Path assets = output.resolve("assets");
      Files.createDirectories(assets);
      for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.assets.entrySet()) {
        this.write(assets, entry.getKey(), entry.getValue().get());
      }

      Path data = output.resolve("data");
      Files.createDirectories(data);
      for (Map.Entry<Identifier, Supplier<byte[]>> entry : this.data.entrySet()) {
        this.write(data, entry.getKey(), entry.getValue().get());
      }
      LOGGER.info("Dumping {} finished.", getName());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
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
    this.lock();
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
    this.waiting.unlock();
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

  @Override
  public Identifier getId() {
    return this.id;
  }

  /**
   * pack.png and that's about it, I think/hope
   *
   * @param segments the segments of the file
   * @return the pack.png image as a stream
   */
  @Override
  public InputSupplier<InputStream> openRoot(String... segments) {
    this.lock();
    Supplier<byte[]> supplier = this.root.get(Arrays.asList(segments));
    if (supplier == null) {
      this.waiting.unlock();
      return null;
    }
    this.waiting.unlock();
    return () -> new ByteArrayInputStream(supplier.get());
  }

  @Nullable
  @Override
  public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
    this.lock();
    Supplier<byte[]> supplier = this.getSys(type).get(id);
    this.waiting.unlock();
    return supplier == null ? null : () -> new ByteArrayInputStream(supplier.get());
  }

  @Override
  public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
    this.lock();
    for (Identifier identifier : this.getSys(type).keySet()) {
      // deleted section: detecting "No resource found for..."
      if (identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(prefix)) {
        consumer.accept(identifier, open(type, identifier));
      }
    }
    this.waiting.unlock();
  }

  //  @Override
  public boolean contains(ResourceType type, Identifier id) {
    this.lock();
    boolean contains = this.getSys(type).containsKey(id);
    this.waiting.unlock();
    return contains;
  }

  @Override
  public Set<String> getNamespaces(ResourceType type) {
    this.lock();
    Set<String> namespaces = new HashSet<>();
    for (Identifier identifier : this.getSys(type).keySet()) {
      namespaces.add(identifier.getNamespace());
    }
    this.waiting.unlock();
    return namespaces;
  }

  /**
   * 本方法与 BRRP 0.7.0 版本根据 ARRP 0.6.2 进行了修改，作者 Devan Kerman。
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
        object.add("description", Text.Serializer.toJsonTree(getDescription()));
        return metaReader.fromJson(object);
      }
      if (KEY_WARNINGS.add(metaReader.getKey())) {
        LOGGER.info("'" + metaReader.getKey() + "' is an unsupported metadata key");
      }
      return null;
    }
  }

  @Override
  public String getName() {
    return "Runtime Resource Pack " + this.id.toString();
  }

  @Override
  public void close() {
    LOGGER.info("Closing {}.", getName());

    // lock
    this.lock();
    if (DUMP) {
      this.dump();
    }

    // unlock
    this.waiting.unlock();
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

  private void lock() {
    if (!this.waiting.tryLock()) {
      if (DEBUG_PERFORMANCE) {
        long start = System.currentTimeMillis();
        this.waiting.lock();
        long end = System.currentTimeMillis();
        LOGGER.warn("Waited " + (end - start) + "ms for lock in RRP: " + this.id);
      } else {
        this.waiting.lock();
      }
    }
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
    if (side == ResourceType.CLIENT_RESOURCES) langMergeable.clear();
  }

  @Override
  public void clearResources() {
    assets.clear();
    data.clear();
    root.clear();
    langMergeable.clear();
  }

  @Override
  public void clearRootResources() {
    root.clear();
  }

  private Map<Identifier, Supplier<byte[]>> getSys(ResourceType side) {
    return side == ResourceType.CLIENT_RESOURCES ? this.assets : this.data;
  }

  private class Memoized<T> implements Supplier<byte[]> {
    private final BiFunction<RuntimeResourcePack, T, byte[]> func;
    private final T path;
    private byte[] data;

    public Memoized(BiFunction<RuntimeResourcePack, T, byte[]> func, T path) {
      this.func = func;
      this.path = path;
    }

    @Override
    public byte[] get() {
      if (this.data == null) {
        this.data = func.apply(RuntimeResourcePackImpl.this, path);
      }
      return this.data;
    }
  }

  @ApiStatus.AvailableSince("0.9.0")
  @Override
  public boolean isAlwaysStable() {
    return true;
  }
}