package net.devtech.arrp.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.animation.JAnimation;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.tags.JTag;
import net.devtech.arrp.util.CallableFunction;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>A resource pack whose assets and data are evaluated at runtime.
 *
 * <p>After creating a runtime resource pack, you should register it in {@link RRPEventHelper}, or {@code RRPCallback}, {@code SidedRRPCallback}, or {@code RRPEvent} so that it can take effect when loading resources.
 */
@SuppressWarnings("unused")
public interface RuntimeResourcePack extends ResourcePack {
  /**
   * The default output path to dump resources.
   */
  Path DEFAULT_OUTPUT = Paths.get("rrp.debug");
  /**
   * Equivalent to {@link RuntimeResourcePackImpl#GSON}.
   */
  @ApiStatus.AvailableSince("0.6.2")
  Gson GSON = RuntimeResourcePackImpl.GSON;

  /**
   * Create a new runtime resource pack with the default supported resource pack version
   */
  @Contract("_ -> new")
  static RuntimeResourcePack create(String id) {
    return new RuntimeResourcePackImpl(new Identifier(id));
  }

  /**
   * Create a new runtime resource pack with the specified resource pack version
   */
  @Contract("_, _ -> new")
  static RuntimeResourcePack create(String id, int version) {
    return new RuntimeResourcePackImpl(new Identifier(id), version);
  }

  /**
   * Create a new runtime resource pack with the default supported resource pack version
   */
  @Contract("_ -> new")
  static RuntimeResourcePack create(Identifier id) {
    return new RuntimeResourcePackImpl(id);
  }

  /**
   * Create a new runtime resource pack with the specified resource pack version
   */
  @Contract("_, _ -> new")
  static RuntimeResourcePack create(Identifier id, int version) {
    return new RuntimeResourcePackImpl(id, version);
  }

  static Identifier id(String string) {
    return new Identifier(string);
  }

  static Identifier id(String namespace, String path) {
    return new Identifier(namespace, path);
  }

  /**
   * Set this to {@code true} will make it throw an exception when a duplicate resource is added.
   */
  @ApiStatus.AvailableSince("0.7.0")
  @Contract(mutates = "this")
  default void setForbidsDuplicateResource(boolean b) {
    // subclasses should override this.
  }

  /**
   * Reads, clones, and recolors the texture at the given path, and puts the newly created image in the given id.
   * <p>
   * <b>If your resource pack is registered at a higher priority than where you expect the texture to be in, Minecraft will
   * be unable to find the asset you are looking for.</b>
   *
   * @param identifier the place to put the new texture
   * @param target     the input stream of the original texture
   * @param pixel      the pixel recolorer
   */
  @Contract(mutates = "this")
  void addRecoloredImage(Identifier identifier, InputStream target, IntUnaryOperator pixel);

  /**
   * Add a language file for the given language.
   * <p>
   * <i>Do not</i> call this method multiple times for a same language, as they will override each other!
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addLang(Identifier identifier, JLang lang);

  /**
   * Multiple calls to this method with the same identifier will merge them into one lang file
   */
  @Contract(mutates = "this")
  void mergeLang(Identifier identifier, JLang lang);

  /**
   * Add a loot table to the runtime resource pack.
   *
   * @param identifier The identifier of the loot table. It is usually in the format of {@code namespace:blocks/path}.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addLootTable(Identifier identifier, JLootTable table);

  @CanIgnoreReturnValue
  @ApiStatus.AvailableSince("0.8.0")
  @Contract(mutates = "this")
  byte[] addLootTable(Identifier identifier, LootTable lootTable);

  /**
   * Add an async resource, which is evaluated off-thread, and does not hold all resource retrieval unlike.
   *
   * @see #async(Consumer)
   */
  @Contract(mutates = "this")
  Future<byte[]> addAsyncResource(ResourceType type,
                                  Identifier identifier,
                                  CallableFunction<Identifier, byte[]> data);

  /**
   * Add resource that is lazily evaluated, which is, evaluated only when required to get, and will not be evaluated again if required to get again.
   */
  @Contract(mutates = "this")
  void addLazyResource(ResourceType type, Identifier path, BiFunction<RuntimeResourcePack, Identifier, byte[]> data);

  /**
   * Add a raw resource to the runtime resource pack.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addResource(ResourceType type, Identifier path, byte[] data);

  /**
   * Add an async root resource, which is evaluated off-thread and does not hold all resource retrieval unlike.
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, ARRP generates a default mcmeta.
   *
   * @see #async(Consumer)
   */
  @Contract(mutates = "this")
  Future<byte[]> addAsyncRootResource(String path,
                                      CallableFunction<String, byte[]> data);

  /**
   * Add a root resource that is lazily evaluated.
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, ARRP generates a default mcmeta.
   */
  @Contract(mutates = "this")
  void addLazyRootResource(String path, BiFunction<RuntimeResourcePack, String, byte[]> data);

  /**
   * Add a raw resource to the root path
   * <p>
   * A root resource is something like pack.png, pack.mcmeta, etc. By default, ARRP generates a default mcmeta.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addRootResource(String path, byte[] data);

  /**
   * Add a custom client-side resource.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addAsset(Identifier id, byte[] data);

  /**
   * Add a custom server data.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addData(Identifier id, byte[] data);

  /**
   * Add a model to this runtime resource pack. It is usually the block model or item model. The identifier is mainly in the form of {@code namespace:block/path} or {@code namespace:item/path}.
   *
   * @param model The model to be added.
   * @param id    The identifier of the model.
   * @see JModel
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addModel(JModel model, Identifier id);

  /**
   * Add a block states file to the runtime resource pack. It defines which model or models will be used and how be used for each variant of the block.
   *
   * @param state      The block states file to be added.
   * @param identifier The identifier of the block states file. It is usually the same as the block id.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  default byte[] addBlockState(@SuppressWarnings("deprecation") JState state, Identifier identifier) {
    return addBlockState(JBlockStates.of(state), identifier);
  }

  /**
   * Add a block states file to the runtime resource pack. It defines which model or models will be used and how be used for each variant of the block.
   *
   * @param state The block states file to be added.
   * @param id    The identifier of the block states file. It is usually the same as the block id.
   * @see JBlockStates
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addBlockState(JBlockStates state, Identifier id);


  @ApiStatus.AvailableSince("0.8.0")
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addBlockState(BlockStateSupplier state, Identifier id);

  /**
   * Adds a texture png.
   * <p>
   * {@code ".png"} is automatically appended to the path.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addTexture(Identifier id, BufferedImage image);

  /**
   * Add an animation json for a texture.
   * <p>
   * {@code ".png.mcmeta"} is automatically appended to the path
   *
   * @see JAnimation
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addAnimation(Identifier id, JAnimation animation);

  /**
   * Add a tag in the specified id.
   * <p>
   * {@code ".json"} is automatically appended to the path.
   *
   * @param id  The identifier of the tag. It contains the specification of the tag type.
   * @param tag The tag to be added.
   * @see JTag
   * @see net.devtech.arrp.json.tags.IdentifiedTag#write(RuntimeResourcePack)
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addTag(Identifier id, JTag tag);

  /**
   * Add a recipe for an item (including block).
   * <p>
   * {@code ".json"} is automatically appended to the path.
   * <p>
   * Please note that you often need a corresponding advancement of the recipe, which is unlocked when you have an ingredient, and grants you with the recipe. More information can be seen in {@link #addRecipeAdvancement} and {@link #addRecipeAndAdvancement(Identifier, String, JRecipe)}.
   *
   * @param id     The {@linkplain Identifier identifier} of the recipe, which is usually the same as the item id, or the item id suffixed with something like {@code _from_stonecutting}.
   * @param recipe The recipe to be added.
   * @see JRecipe
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addRecipe(Identifier id, JRecipe recipe);

  /**
   * <p>Add an advancement of that recipe. In BRRP, the advancement builder is integrated in the recipe object. You may have to add common advancement elements with {@link JRecipe#addInventoryChangedCriterion}, or just modify the {@link JRecipe#advancementBuilder}, otherwise the advancement will not be generated.
   * <p>Usually the advancement is granted when you unlock the recipe or obtains the ingredient, and rewards you to unlock that recipe. For example, if you obtain an <i>oak planks</i>, then the advancement {@code minecraft:recipes/building_blocks/oak_planks} is achieved, rewarding you to unlock the recipe of {@code minecraft:oak_planks}. There are some exception situations: For example, the recipe of boat is not obtained when you get their corresponding planks, but when you enter water.
   * <p>The parameter <i>recipeId</i> is used because it is required in {@link JRecipe#prepareAdvancement(Identifier)}, and <i>it is usually not equal to</i> the advancement id. You must tell the advancement which recipe will be unlocked to you.
   * <p>The parameter <i>advancementId</i> is typically prefixed with <code style="color:maroon">recipes/<i>itemGroup</i></code> (for versions below 1.19.3). In the convention of vanilla Minecraft, the identifier of the recipe is <code style="color:maroon"><i>namespace</i>:<i>path</i></code>, which is typically the same as the item itself. But the identifier of the advancement is <code style="color:maroon"><i>namespace</i>:recipes/<i>itemGroup</i>/<i>path</i></code>.
   * <p>You can create advancement id with {@link net.devtech.arrp.generator.ItemResourceGenerator#getAdvancementIdForRecipe} or {@link net.devtech.arrp.generator.ResourceGeneratorHelper#getAdvancementIdForRecipe}.
   * <p>If you feel tired of calling both {@link #addRecipe} and {@code addRecipeAdvancement}, you can consider {@link #addRecipeAndAdvancement(Identifier, String, JRecipe)}.
   *
   * @param recipeId                    The identifier of the recipe.
   * @param advancementId               The identifier of the advancement the corresponds to the recipe, usually prefixed with {@code "recipes/"} in the path.
   * @param recipeContainingAdvancement The recipe that contains the advancement ({@link JRecipe#advancementBuilder}, which can be added through {@link JRecipe#addInventoryChangedCriterion}). If that advancement has no criteria, it will be ignored and {@code null} will be returned.
   */
  @ApiStatus.AvailableSince("0.6.2")
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  default byte[] addRecipeAdvancement(Identifier recipeId, Identifier advancementId, JRecipe recipeContainingAdvancement) {
    final Advancement.Builder advancement = recipeContainingAdvancement.asAdvancement();
    if (advancement != null && !advancement.getCriteria().isEmpty()) {
      recipeContainingAdvancement.prepareAdvancement(recipeId);
      return addAdvancement(advancementId, advancement);
    } else {
      return null;
    }
  }

  /**
   * <p>This method will add the recipe <i>as well as</i> the advancement of the recipe to the runtime resource pack. Usually, the identifier of the advancement is the recipe identifier prefixed with <code>recipes/<i>itemGroup</i></code>, for example, the identifier of the <i>recipe</i> or oak_planks is <code style=color:maroon>minecraft:oak_planks</code> and the identifier of the <i>advancement</i> of the oak_planks recipes is <code style=color:maroon>minecraft:recipes/building_blocks/oak_planks</code>. This method just follows the convention of vanilla Minecraft's recipe and advancement, and you should <i>manually</i> specify the group name.
   * <p>Besides, you can also use {@link net.devtech.arrp.generator.ResourceGeneratorHelper#getAdvancementIdForRecipe} to create the identifier of the advancement. In this case you should specify which item it is, and do not call this method.
   *
   * @param recipeId                    The identifier of the recipe. It is commonly the same as the identifier of the item, optionally suffixed with additional expressions like {@code _from_stonecutting}.
   * @param groupName                   The name of the item group. This is used to specify in the identifier of the advancement. The identifier of the advancement will be the {@code recipeId} prefixed with <code>recipes/<i>groupName</i>/</code>. The group name can be null.
   * @param recipeContainingAdvancement The recipe that contains the advancement ({@link JRecipe#advancementBuilder}, which can be added through {@link JRecipe#addInventoryChangedCriterion}). If that advancement has no criteria, it will be ignored but the recipe will not throw error.
   * @see net.devtech.arrp.generator.ItemResourceGenerator#writeRecipes(RuntimeResourcePack)
   */
  @ApiStatus.AvailableSince("0.7.0")
  @Contract(mutates = "this")
  default void addRecipeAndAdvancement(Identifier recipeId, @Nullable String groupName, JRecipe recipeContainingAdvancement) {
    addRecipe(recipeId, recipeContainingAdvancement);
    addRecipeAdvancement(recipeId, recipeId.brrp_prepend("recipes/" + (StringUtils.isEmpty(groupName) ? "" : groupName + "/")), recipeContainingAdvancement);
  }

  @ApiStatus.AvailableSince("0.8.0")
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addRecipe(Identifier id, RecipeJsonProvider recipe);

  /**
   * Add an advancement to the runtime resource pack.
   * <p>
   * The extension {@code ".json"} is automatically appended to the path.
   *
   * @param id          The {@linkplain Identifier identifier} of the advancement.
   * @param advancement The advancement to be added.
   */
  @CanIgnoreReturnValue
  @Contract(mutates = "this")
  byte[] addAdvancement(Identifier id, Advancement.Builder advancement);

  /**
   * Invokes the action on the RRP executor. RRPs are thread-safe, so you can create expensive assets here. All resources
   * are blocked until all async tasks are completed.
   * <p>
   * Calling in this function from itself will result in an infinite loop
   *
   * @see #addAsyncResource(ResourceType, Identifier, CallableFunction)
   */
  Future<?> async(Consumer<RuntimeResourcePack> action);

  /**
   * Write the runtime resource pack as local files, as if it is a regular resource pack or data pack, making you available to directly visit its content.
   */
  default void dump() {
    this.dump(DEFAULT_OUTPUT);
  }

  /**
   * Write the runtime resource pack as local files, as if it is a regular resource pack or data pack, making you available to directly visit its content.
   *
   * @param path The path to write the resource pack directly.
   */
  void dumpDirect(Path path);

  /**
   * Load a regular resource pack or data pack from a local path, and convert into a runtime resource pack.
   *
   * @param path The path of the regular resource pack or data pack.
   * @throws IOException if thrown when reading files.
   */
  void load(Path path) throws IOException;

  /**
   * Write the runtime resource pack as local files, making you available to directly visit its content.
   *
   * @deprecated use {@link #dump(Path)}
   */
  @Deprecated
  void dump(File file);

  /**
   * Write the runtime resource pack as local files, as if it is a regular resource pack or data pack, making you available to directly visit its content.
   *
   * @param path The path to write the resource pack. In the path, the folder named with identifier will be created.
   */
  default void dump(Path path) {
    Identifier id = this.getId();
    Path folder = path.resolve(id.getNamespace() + '_' + id.getPath());
    this.dumpDirect(folder);
  }

  /**
   * Write the runtime resource pack as a local zip file, making you available to directly visit its content.
   *
   * @see ByteBufOutputStream
   */
  void dump(ZipOutputStream stream) throws IOException;

  /**
   * Load a regular resource pack or data pack from a zip file, and convert it to this runtime resource pack.
   *
   * @see ByteBufInputStream
   */
  void load(ZipInputStream stream) throws IOException;

  @Contract(pure = true)
  Identifier getId();

  /**
   * Clear the resources of the runtime resource pack in the specified side.
   * <p>
   * Root resources will not be cleared. Language file is treated as client resources.
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
  @ApiStatus.AvailableSince("0.7.0")
  @Contract(mutates = "this")
  void clearRootResources();

  @ApiStatus.AvailableSince("0.9.0")
  default Text getDisplayName() {
    return new TranslatableText("brrp.pack_display_name", getId());
  }

  @ApiStatus.AvailableSince("0.9.0")
  default Text getDescription() {
    return new TranslatableText("brrp.pack_description");
  }
}