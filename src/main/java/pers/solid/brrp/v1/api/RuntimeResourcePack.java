package pers.solid.brrp.v1.api;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.recipe.*;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.*;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.function.FailableRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;
import pers.solid.brrp.v1.JsonSerializers;
import pers.solid.brrp.v1.RRPEventHelper;
import pers.solid.brrp.v1.gui.DumpScreen;
import pers.solid.brrp.v1.gui.RRPConfigScreen;
import pers.solid.brrp.v1.gui.RegenerateScreen;
import pers.solid.brrp.v1.impl.RuntimeResourcePackImpl;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.tag.IdentifiedTagBuilder;
import pers.solid.brrp.v1.tag.ObjectTagBuilder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>A resource pack whose assets and data are evaluated at runtime. You can use {@link #create} to create it.</p>
 * <p>After creating a runtime resource pack, you should register it in {@link RRPEventHelper}, or {@code RRPCallback}, {@code SidedRRPCallback}, or {@code RRPEvent} so that it can take effect when loading resources. Here hs an example:</pre>
 * <pre>{@code
 * // create the runtime resource pack
 * RuntimeResourcePack myModPack = RuntimeResourcePack.create(new Identifier("my_mod", "example_pack"));
 *
 * // write some things into the pack
 * myModPack.addLang(new Identifier("my_mod", "zh_cn"), LanguageProvider.create().add(...);
 * myModPack.addLootTable(MyModBlocks.EXAMPLE_BLOCK.getLootTableId(), LootTable.builder()...().build();
 * ...
 *
 * // register the runtime resource pack
 * // the following code can be used for both Forge and Fabric:
 * RRPEventHelper.BEFORE_VANILLA.registerPack(myModPack);
 * // the following code can only be used for Fabric:
 * RRPCallback.BEFORE_VANILLA.register(r -> r.add(myModPack));
 * // the following code can only be used for Forge:
 * FMLJavaModLoadingContext.get().getModEventBus().addListener((RRPEvent event) -> event.addPack(myModPack));
 * }
 * </pre>
 * <p>The runtime resource pack should be registered to events so it will be loaded, otherwise it will be ignored.</p>
 * <p>The runtime resource pack by default does not allow adding duplicate resources, which will throw an error. You can use {@link #setAllowsDuplicateResource} to change its behaviours. Besides, you can config how to re-generate its content {@link #setRegenerationCallback} so that you can hot-swap and then regenerate resources in the mod's config screen.</p>
 */
@SuppressWarnings("unused")
public interface RuntimeResourcePack extends ResourcePack {
  /**
   * The default output path to dump resources.
   */
  Path DEFAULT_OUTPUT = Paths.get("rrp.debug");

  /**
   * The GSONs used to serialize objects to JSON.
   */
  Gson GSON = LootGsons.getTableGsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .enableComplexMapKeySerialization()
      .registerTypeHierarchyAdapter(JsonSerializable.class, JsonSerializable.SERIALIZER)
      .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer())
      .registerTypeHierarchyAdapter(StringIdentifiable.class, JsonSerializers.STRING_IDENTIFIABLE)
      .registerTypeHierarchyAdapter(Vector3f.class, JsonSerializers.VECTOR_3F)
      .registerTypeHierarchyAdapter(Either.class, JsonSerializers.EITHER)
      .registerTypeHierarchyAdapter(RecipeJsonProvider.class, JsonSerializers.RECIPE_JSON_PROVIDER)
      .setPrettyPrinting()
      .create();
  Logger LOGGER = LogUtils.getLogger();

  /**
   * Create a new runtime resource pack with the default supported resource pack version
   */
  @Contract("_ -> new")
  static RuntimeResourcePack create(Identifier id) {
    return new RuntimeResourcePackImpl(id);
  }

  /**
   * Serialize an object to a byte array, used for runtime resource packs.
   */
  static byte[] serialize(Object object, @NotNull Gson gson) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
    gson.toJson(object, writer);
    try {
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return stream.toByteArray();
  }

  /**
   * Serialize an object to a byte array, using the default GSON for the runtime resource pack.
   */
  static byte[] defaultSerialize(Object object) {
    return serialize(object, GSON);
  }

  /**
   * Serialize the object to a byte array. You can override this method for your own serialization.
   */
  default byte[] serialize(Object object) {
    return defaultSerialize(object);
  }

  /**
   * The version of the pack, which will be used as a metadata for Minecraft. Minecraft uses it to identify whether it's too old or new. But do not worry — the mod can automatically configure it.
   */
  @Contract(pure = true)
  int getPackVersion();

  /**
   * Set the pack version. This is not required to be manually set.
   */
  @Contract(mutates = "this")
  void setPackVersion(int packVersion);

  /**
   * To avoid some potential bugs, the runtime resource pack does not allow duplicate resources. However, you can set this to {@code true} to prevent the exception.
   */
  @Contract(mutates = "this")
  void setAllowsDuplicateResource(boolean b);

  /**
   * Reads, clones, and recolors the texture at the given path, and puts the newly created image in the given id.
   * <p>
   * <strong>If your resource pack is registered at a higher priority than where you expect the texture to be in, Minecraft will
   * be unable to find the asset you are looking for.</strong>
   *
   * @param identifier the place to put the new texture
   * @param target     the input stream of the original texture
   * @param pixel      the pixel recolorer
   */
  @ApiStatus.Experimental
  @Contract(mutates = "this")
  void addRecoloredImage(Identifier identifier, InputStream target, IntUnaryOperator pixel);

  @Contract(mutates = "this")
  byte[] addLang(Identifier identifier, byte[] serializedData);

  /**
   * <p>Add a language file for the given language. This is an example:</p>
   * <pre>{@code
   * pack.addLang(new Identifier("my_mod", "zh_cn"), LanguageProvider.create().add(...));
   * }</pre>
   * <p><em>Do not</em> call this method multiple times for a same language, as they will override each other!</pre>
   * <pre>{@code
   * // wrong:
   * pack.addLang(new Identifier("my_mod", "zh_cn"), LanguageProvider.create().add("key1", "value1");
   * pack.addLang(new Identifier("my_mod", "zh_cn"), LanguageProvider.create().add("key2", "value2");
   *
   * // correct:
   * pack.addLang(new Identifier("my_mod", "zh_cn"), LanguageProvider.create().add("key1", "value1").add("key2", "value2"));
   * }</pre>
   *
   * @param identifier The identifier of the language file. Usually, the path is the language code (such as {@code en_us}, {@code zh_cn}). The namespace does not matter, but it usually should be your mod's namespace.
   * @param lang       The {@link LanguageProvider} object.
   */
  @Contract(mutates = "this")
  default byte[] addLang(Identifier identifier, LanguageProvider lang) {
    return addLang(identifier, serialize(lang.content()));
  }

  @Contract(mutates = "this")
  byte[] addLootTable(Identifier identifier, byte[] serializedData);

  /**
   * Add a loot table. It is usually used to provide loot tables for your mod.
   *
   * @param identifier The identifier of the loot table. Please refer to {@link AbstractBlock#getLootTableId()}.
   * @param lootTable  The loot table object. It can be created through {@link LootTable#builder()}. Please also refer to {@link net.minecraft.data.server.loottable.LootTableProvider} for some methods to conveniently create loot tables.
   */
  @Contract(mutates = "this")
  default byte[] addLootTable(Identifier identifier, LootTable lootTable) {
    return addLootTable(identifier, serialize(lootTable));
  }

  /**
   * Add a loot table. It is usually used to provide loot tables for your mod.
   *
   * @param identifier The identifier of the loot table. Please refer to {@link AbstractBlock#getLootTableId()}.
   * @param lootTable  The loot table (builder) object. It can be created through {@link LootTable#builder()}. Please also refer to {@link net.minecraft.data.server.loottable.LootTableProvider} for some methods to conveniently create loot tables.
   */
  @Contract(mutates = "this")
  default byte[] addLootTable(Identifier identifier, LootTable.Builder lootTable) {
    return this.addLootTable(identifier, lootTable.build());
  }

  /**
   * Add an async resource, which is evaluated off-thread, and does not hold all resource retrieval unlike.
   *
   * @see #async(Consumer)
   */
  @Contract(mutates = "this")
  @ApiStatus.Experimental
  Future<byte[]> addAsyncResource(ResourceType type,
                                  Identifier identifier,
                                  FailableFunction<Identifier, byte[], Exception> data);

  /**
   * Add resource that is lazily evaluated, which is, evaluated only when required to get, and will not be evaluated again if required to get again.
   */
  @Contract(mutates = "this")
  @ApiStatus.Experimental
  void addLazyResource(ResourceType type, Identifier path, BiFunction<RuntimeResourcePack, Identifier, byte[]> data);

  /**
   * Add a raw resource to the runtime resource pack.
   */
  @Contract(mutates = "this")
  byte[] addResource(ResourceType type, Identifier path, byte[] data);

  /**
   * Add an async root resource, which is evaluated off-thread and does not hold all resource retrieval unlike.
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, default mcmeta will be generated.
   *
   * @see #async(Consumer)
   */
  @Contract(mutates = "this")
  Future<byte[]> addAsyncRootResource(String path,
                                      FailableFunction<String, byte[], Exception> data);

  /**
   * Add a root resource that is lazily evaluated.
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, default mcmeta will be generated.
   */
  @Contract(mutates = "this")
  void addLazyRootResource(String path, BiFunction<RuntimeResourcePack, String, byte[]> data);

  /**
   * Add a raw resource to the root path
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, default mcmeta will be generated.
   */
  @Contract(mutates = "this")
  byte[] addRootResource(String path, byte[] data);

  /**
   * Add a custom client-side resource.
   */
  @Contract(mutates = "this")
  byte[] addAsset(Identifier id, byte[] data);

  /**
   * Add a custom server data.
   */
  @Contract(mutates = "this")
  byte[] addData(Identifier id, byte[] data);

  @Contract(mutates = "this")
  byte[] addBlockState(Identifier id, byte[] serializedData);

  /**
   * Add a block state to this runtime resource packs. Here is an example:
   * <pre>{@code
   * pack.addBlockState(MyModBlocks.EXAMPLE_BLOCK, BlockStateModelGenerator.createSingletonBlockState(MyModBlocks.EXAMPLE_BLOCK, new Identifier("my_mod", "block/example_block"));
   * }</pre>
   *
   * @param id    The id of the block state, usually identical to the id of the block.
   * @param state The {@link BlockStateSupplier} file. You can also easily create them using methods in {@link net.minecraft.data.client.BlockStateModelGenerator}.
   */
  @Contract(mutates = "this")
  default byte[] addBlockState(Identifier id, @NotNull BlockStateSupplier state) {
    return addBlockState(id, serialize(state.get()));
  }

  /**
   * Adds a texture png.
   * <p>
   * {@code ".png"} is automatically appended to the path.
   */
  @Contract(mutates = "this")
  byte[] addTexture(Identifier id, BufferedImage image);

  /**
   * Add a tag to the pack.
   *
   * @param fullId         The full resource location of the pack. For example, <code>new Identifier("minecraft", "blocks/stairs")</code>, <code>new Identifier("minecraft", "functions/tick")</code>.
   * @param serializedData The serialized tag data.
   */
  @Contract(mutates = "this")
  byte[] addTag(Identifier fullId, byte[] serializedData);

  @Contract(mutates = "this")
  default byte[] addTag(Identifier fullId, TagBuilder tagBuilder) {
    return addTag(fullId, serialize(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false)).getOrThrow(false, LOGGER::error)));
  }

  /**
   * Add a tag to the pack. It uses the vanilla {@link TagKey} object, which contains the registry type and the identifier, so the full resource location can be determined.
   *
   * @param tagKey     The vanilla {@link TagKey} object. It is usually created through {@link TagKey#of}. You can find vanilla ones in classes like {@link BlockTags} and {@link ItemTags}.
   * @param tagBuilder The vanilla {@link TagBuilder} object. You may also find {@link ObjectTagBuilder} and {@link IdentifiedTagBuilder} provided by this mod.
   * @param <T>        The type parameter of the tag.
   */
  @Contract(mutates = "this")
  <T> byte[] addTag(TagKey<T> tagKey, TagBuilder tagBuilder);

  /**
   * Add a tag to the pack. As {@link IdentifiedTagBuilder} contains the tag id, you do not need to specify it.
   *
   * @param identifiedTagBuilder The {@link IdentifiedTagBuilder} object. It contains both the registry the tag uses, the tag id and the tag content.
   */
  @Contract(mutates = "this")
  default <T> byte[] addTag(IdentifiedTagBuilder<T> identifiedTagBuilder) {
    return addTag(TagKey.of(identifiedTagBuilder.registry.getKey(), identifiedTagBuilder.identifier), identifiedTagBuilder);
  }

  @Contract(mutates = "this")
  byte[] addAnimation(Identifier id, byte[] serializedData);

  /**
   * Add an animation json for a texture. <strong>Note: {@link AnimationResourceMetadata} is client-only, so you should only use it in the client distribution.</strong> There is an example:
   * <pre>{@code
   * if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
   *   AnimationResourceMetadata metadata = new AnimationResourceMetadata(...);
   *   pack.addAnimation(new Identifier("my_mod", "block/my_texture"), metadata);
   * }
   * </pre>
   * }
   *
   * @param id        The id of the animation json. You do not need to include the suffix {@code .json} in it.
   * @param animation The metadata object.
   */
  @Contract(mutates = "this")
  @Environment(EnvType.CLIENT)
  default byte[] addAnimation(Identifier id, AnimationResourceMetadata animation) {
    return this.addAnimation(id, serialize(animation));
  }

  @Contract(mutates = "this")
  byte[] addRecipe(Identifier id, byte[] serializedData);

  /**
   * Add a recipe to the runtime resource pack. The recipe id is usually same to the item id, but sometimes append with some extra information.
   *
   * @param id     The id of the recipe, which is usually same to the item id, but can sometimes be suffixed with other string. Example: {@code new Identifier("my_mod", "strange_stone_stairs"}}, {@code new Identifier("my_mod", "strange_stone_from_stonecutting")}.
   * @param recipe The {@link RecipeJsonProvider} object. You may conveniently create it using methods in {@link RecipeProvider}.
   */
  @Contract(mutates = "this")
  default byte[] addRecipe(Identifier id, RecipeJsonProvider recipe) {
    return addRecipe(id, serialize(recipe));
  }

  /**
   * Add a recipe <em>as well as</em> a corresponding advancement to obtain that recipe. Both the recipe id and the advancement id are stored in the {@link RecipeJsonProvider}, so you do not need to specify it here. The recipe must have a {@link RecipeCategory}, or it will throw an error when fetching the advancement id.
   *
   * @param recipeJsonProvider The {@link RecipeJsonProvider} object. You may conveniently create it using methods in {@link RecipeProvider}.
   */
  @Contract(mutates = "this")
  default void addRecipeAndAdvancement(@NotNull RecipeJsonProvider recipeJsonProvider) {
    addRecipe(recipeJsonProvider.getRecipeId(), recipeJsonProvider);
    addAdvancement(recipeJsonProvider.getAdvancementId(), serialize(recipeJsonProvider.toAdvancementJson()));
  }

  /**
   * Add a recipe <em>as well as</em> a corresponding advancement to obtain that recipe. You may confirm it has a criterion to obtain the recipe of it will throw an error, unless you call {@link pers.solid.brrp.v1.recipe.RecipeJsonBuilderExtension#setBypassesValidation(boolean)} to bypass it.
   *
   * @param recipeId          The id of the recipe.
   * @param recipeJsonBuilder The {@link RecipeJsonBuilder}. The id of the advancement will be determined by {@link CraftingRecipeJsonBuilder#offerTo}.
   */
  @Contract(mutates = "this")
  default void addRecipeAndAdvancement(Identifier recipeId, @NotNull CraftingRecipeJsonBuilder recipeJsonBuilder) {
    recipeJsonBuilder.offerTo(this::addRecipeAndAdvancement, recipeId);
  }

  /**
   * Add a recipe <em>as well as</em> a corresponding advancement to obtain that recipe. You may confirm it has a criterion to obtain the recipe of it will throw an error, unless you call {@link pers.solid.brrp.v1.recipe.RecipeJsonBuilderExtension#setBypassesValidation(boolean)} to bypass it.
   *
   * @param recipeId          The id of the recipe.
   * @param recipeJsonBuilder The {@link RecipeJsonBuilder}. The id of the advancement will be determined by {@link SmithingTransformRecipeJsonBuilder#offerTo}.
   */
  @Contract(mutates = "this")
  default void addRecipeAndAdvancement(Identifier recipeId, @NotNull SmithingTransformRecipeJsonBuilder recipeJsonBuilder) {
    recipeJsonBuilder.offerTo(this::addRecipeAndAdvancement, recipeId);
  }

  /**
   * Add a recipe <em>as well as</em> a corresponding advancement to obtain that recipe. You may confirm it has a criterion to obtain the recipe of it will throw an error, unless you call {@link pers.solid.brrp.v1.recipe.RecipeJsonBuilderExtension#setBypassesValidation(boolean)} to bypass it.
   *
   * @param recipeId          The id of the recipe.
   * @param recipeJsonBuilder The {@link RecipeJsonBuilder}. The id of the advancement will be determined by {@link SmithingTrimRecipeJsonBuilder#offerTo}.
   */
  @Contract(mutates = "this")
  default void addRecipeAndAdvancement(Identifier recipeId, @NotNull SmithingTrimRecipeJsonBuilder recipeJsonBuilder) {
    recipeJsonBuilder.offerTo(this::addRecipeAndAdvancement, recipeId);
  }

  /**
   * Add a model to the runtime resource pack. It is usually used in the client side.
   *
   * @param id    The id of the model. For block models, it is referred to in the {@link BlockStateModelGenerator}. Some examples: {@code new Identifier("my_mod", "block/my_stone_slab_top")}, {@code new Identifier("my_mod", "item/my_item")}.
   * @param model The {@link ModelJsonBuilder} object.
   */
  @Contract(mutates = "this")
  default byte[] addModel(Identifier id, ModelJsonBuilder model) {
    return addModel(id, serialize(model));
  }

  @Contract(mutates = "this")
  byte[] addModel(Identifier id, byte[] serializedData);

  /**
   * Add an advancement to the runtime resource pack. The extension {@code ".json"} is automatically appended to the path.
   *
   * @param id          The {@linkplain Identifier identifier} of the advancement.
   * @param advancement The advancement to be added.
   */
  @Contract(mutates = "this")
  default byte[] addAdvancement(Identifier id, Advancement.Builder advancement) {
    return addAdvancement(id, serialize(advancement.toJson()));
  }

  @Contract(mutates = "this")
  byte[] addAdvancement(Identifier id, byte[] serializedData);

  /**
   * <p>Invokes the action on the RRP executor. RRPs are thread-safe, so you can create expensive assets here. All resources are blocked until all async tasks are completed.</p>
   * <p>Calling in this function from itself will result in an infinite loop.</p>
   *
   * @see #addAsyncResource(ResourceType, Identifier, FailableFunction)
   */
  @ApiStatus.Experimental
  Future<?> async(Consumer<RuntimeResourcePack> action);

  /**
   * Write the runtime resource pack as local files, as if it is a regular resource pack or data pack, making you available to directly visit its content.
   */
  default void dumpToDefaultPath() {
    this.dump(DEFAULT_OUTPUT);
  }

  /**
   * Write the runtime resource pack as local files, allowing specifying the resource type to dump, and stats to record.
   *
   * @param path             The path to write the resource pack directly.
   * @param dumpResourceType The resource type to be dumped. If it is null, both client resource and server data will be dumped, which is the default situation.
   * @param stat             The array used to store stats. It should contain three elements: First to store root resources, second to store server data, and third to store client resources.
   */
  @Contract(mutates = "param3")
  void dumpInPath(Path path, @Nullable ResourceType dumpResourceType, int @Nullable [] stat);

  /**
   * Load a regular resource pack or data pack from a local path, and convert into a runtime resource pack.
   *
   * @param path The path of the regular resource pack or data pack.
   * @throws IOException if thrown when reading files.
   */
  void load(Path path) throws IOException;

  /**
   * Load a regular resource pack or data pack from a zip file, and convert it to this runtime resource pack.
   *
   * @see ByteBufInputStream
   */
  void load(ZipInputStream stream) throws IOException;

  /**
   * Write all resources in the runtime resource pack as local files, as if it is a regular resource pack or data pack, making you available to directly visit its content.
   *
   * @param path The path to write the resource pack. In the path, the folder named with identifier will be created.
   */
  default void dump(@NotNull Path path) {
    Identifier id = this.getId();
    this.dumpInPath(path.resolve(id.getNamespace() + '/' + id.getPath()), null, null);
  }

  /**
   * Write the runtime resource pack as a local zip file, making you available to directly visit its content.
   *
   * @see ByteBufOutputStream
   */
  void dump(ZipOutputStream stream) throws IOException;

  /**
   * The method is used in {@link RRPConfigScreen} and {@link RegenerateScreen}. If it has the regeneration callback, no matter it is client-only, server-only, or both-side, the "Regenerate" button will be enabled.
   *
   * @see #regenerate()
   */
  @Contract(pure = true)
  default boolean hasRegenerationCallback() {
    return hasSidedRegenerationCallback(ResourceType.CLIENT_RESOURCES) || hasSidedRegenerationCallback(ResourceType.SERVER_DATA) || hasSidedRegenerationCallback(null);
  }

  /**
   * The method is used in {@link RegenerateScreen}, which determines whether it can dump pack for only one side. A runtime resource pack may be able to regenerate all resources, but not able to regenerate only client or only server. In this case, in the {@link RegenerateScreen}, the button to regenerate all resources is enabled, but to regenerate sided resource is disabled.
   *
   * @param resourceType The type of resource.
   */
  @Contract(pure = true)
  boolean hasSidedRegenerationCallback(@Nullable ResourceType resourceType);

  /**
   * <p>Set how to regenerate resources so that in the {@link RegenerateScreen} you can click the button and regenerate all resources for it. The usage of this method is, in the development, you may have modified some code and want to regenerate resources. Therefore, you can go to the {@link RegenerateScreen} through {@link RRPConfigScreen} to regenerate its resources, and then reload the resource packs or data packs to apply changes.</p>
   * <p>Invoking this method does not run the {@code regenerationCallback}. It will be run when you click the "Regenerate" button in the {@link RegenerateScreen}. When regenerating resources, you may have to clear existing resources before regenerating. Here is an example:</p>
   * <pre>{@code
   *   private static final RuntimeResourcePack MY_PACK = ...;
   *
   *   private static void generate() {
   *     MY_PACK.addModel(xxx ...);
   *     MY_PACK.addLootTable(xxx ...);
   *   }
   *
   *   public static void onInitialize() {
   *     generate();
   *     MY_PACK.setRegenerationCallback(() -> {
   *       MY_PACK.clearResources();
   *       generate();
   *     });
   *     RRPCallback.BEFORE_VANILLA.register(s -> s.add(MY_PACK));
   *   }
   * }
   * </pre>
   * Of course, you can invoke {@link #regenerate()} during initialization:
   * <pre>{@code
   *   private static final RuntimeResourcePack MY_PACK = ...;
   *
   *   public static void onInitialize() {
   *     MY_PACK.setRegenerationCallback(() -> {
   *       MY_PACK.clearResources();
   *       MY_PACK.addModel(xxx ...);
   *       MY_PACK.addLootTable(xxx ...);
   *     });
   *     MY_PACK.regenerate();
   *     RRPCallback.BEFORE_VANILLA.register(s -> s.add(MY_PACK));
   *   }
   * }
   * </pre>
   *
   * @param regenerationCallback The callback that will be run when regenerating resources.
   * @see #setSidedRegenerationCallback
   */
  @Contract(mutates = "this")
  void setRegenerationCallback(FailableRunnable<InterruptedException> regenerationCallback);

  /**
   * <p>Set how to regenerate resources for a specific resource type. If you have set sided regeneration callbacks for both client resources and server data, then you won't need to invoke {@link #setRegenerationCallback}.</p>
   * <p>This is an example:</p>
   * <pre>{@code
   *   private static final RuntimeResourcePack MY_PACK = ...;
   *
   *   @Environment(EnvType.CLIENT)
   *   private static void generateClientResources() {
   *     MY_PACK.addModel(xxx ...);
   *   }
   *
   *   private static void generateServerData() {
   *     MY_PACK.addLootTable(xxx ...);
   *   }
   *
   *   public static void onInitialize() {
   *     generateServerData();
   *     MY_PACK.setSidedRegenerationCallback(EnvType.SERVER_DATA, () -> {
   *       MY_PACK.clearResources(ResourceType.SERVER_DATA);
   *       generateServerData();
   *     });
   *     if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
   *       generateClientResources();
   *       MY_PACK.setSidedRegenerationCallback(EnvType.CLIENT_RESOURCES, () -> {
   *         MY_PACK.clearResources(ResourceType.CLIENT_RESOURCES);
   *         generateClientResources();
   *       }
   *     }
   *     RRPCallback.BEFORE_VANILLA.register(s -> s.add(MY_PACK));
   *   }
   * }
   * </pre>
   * <p>In this case, in the {@link DumpScreen}, three buttons to regenerate sided resources or all resources, will all be enabled.</p>
   *
   * @param resourceType         The type of the resource.
   * @param regenerationCallback The callback that will be run when regenerating the side of resources.
   * @see #setRegenerationCallback
   */
  @Contract(mutates = "this")
  void setSidedRegenerationCallback(@NotNull ResourceType resourceType, FailableRunnable<InterruptedException> regenerationCallback);

  /**
   * Regenerate all resources in this runtime resource pack. <strong>It is available only if you have invoked {@link #setRegenerationCallback(FailableRunnable)} or {@link #setSidedRegenerationCallback(ResourceType, FailableRunnable)}, or the method will do nothing.</strong> If you click the buttons to regenerate resources in the {@link RegenerateScreen}, this method will be invoked.
   *
   * @throws InterruptedException Sometimes the resource generation may be interrupted, because it is invoked in a new thread in the {@link RegenerateScreen}. Sometimes you may need to handle it.
   */
  void regenerate() throws InterruptedException;

  /**
   * Regenerate resources for a one resource type. <strong>It is available only if you have invoked {@link #setSidedRegenerationCallback(ResourceType, FailableRunnable)}, or the method will do nothing.
   *
   * @param resourceType The type of resources to be regenerated.
   * @throws InterruptedException Sometimes the resource generation may be interrupted, because it is invoked in a new thread in the {@link RegenerateScreen}. Sometimes you may need to handle it.
   */
  void regenerateSided(@NotNull ResourceType resourceType) throws InterruptedException;

  @Contract(pure = true)
  Identifier getId();

  /**
   * Clear the resources of the runtime resource pack in the specified side. Root resources will not be cleared. Language files are treated as client resources.
   *
   * @param side The side (client or server) of resource to be cleared.
   */
  @Contract(mutates = "this")
  void clearResources(ResourceType side);

  /**
   * Clear all resources of this runtime resource pack, including both client and server, and as well as root resources.
   */
  @Contract(mutates = "this")
  void clearResources();

  /**
   * Clear root resources of this runtime resource pack.
   */
  @Contract(mutates = "this")
  void clearRootResources();

  /**
   * @return The display name of the pack, which may be shown in the {@link RRPConfigScreen}.
   */
  @Contract(pure = true)
  default Text getDisplayName() {
    return Text.translatable("brrp.pack.defaultName", getId());
  }

  /**
   * Set the display name of the pack, which may be shown in the {@link RRPConfigScreen}.
   *
   * @param name The display name, which is a text component and can be localized.
   */
  @Contract(mutates = "this")
  void setDisplayName(Text name);

  /**
   * @return The description of the pac, which may be shown in the {@link RRPConfigScreen}.
   */
  @Contract(pure = true)
  @Nullable Text getDescription();

  /**
   * Set the description of the pack, which may be shown in the {@link RRPConfigScreen}.
   *
   * @param description The description, which is a text component and can be localized.
   */
  @Contract(mutates = "this")
  void setDescription(Text description);

  @Contract(pure = true)
  int numberOfClientResources();

  @Contract(pure = true)
  int numberOfServerData();

  @Contract(pure = true)
  int numberOfRootResources();
}