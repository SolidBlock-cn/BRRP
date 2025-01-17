package pers.solid.brrp.v1.generator;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

/**
 * <p>This interface is used for items.</p>
 * <p>Your custom item class can implement this interface, and override some methods you need. You can also implement this interface on your custom class.</p>
 * <p>This interface is divided into three parts:</p>
 * <ul>
 *   <li>general part: to get the identifier of this instance.</li>
 *   <li>client part: methods related to generating and writing client assets. It's <em>highly recommended but not required</em> to annotate the methods as {@code @}{@link net.fabricmc.api.Environment Environment}<code>({@link net.fabricmc.api.EnvType#CLIENT EnvType.CLIENT})</code> or <code>@OnlyIn(Dist.CLIENT)</code>, because they are only used in client distribution. When running on a dedicated server, they should be ignored.</li>
 *   <li>server part: methods related to generating and writing server data. Please do not annotate them with {@code @Environment(EnvType.SERVER)}, unless you're sure to do so, as they will be used in client distribution.</li>
 * </ul>
 * <p>Most "get" methods are nullable, which means, when writing (in those "write" methods) the values into the runtime resource pack, these null values will be ignored. When overriding these "get" methods, you can annotate @NotNull if you're sure that the values are not null.</p>
 * <p>To generate the resources to your runtime resource pack, you can call {@link #writeAssets(RuntimeResourcePack)} or {@link #writeData(RuntimeResourcePack)}.</p>
 */
@SuppressWarnings("unused")
public interface ItemResourceGenerator {
  /**
   * This map is used for {@link #getRecipeCategory()}, in cases you don't want to override that category. It will be used only for data generation, and allows {@code null} values. Notice that other mods may use it.
   *
   * @see #getRecipeCategory()
   * @see #setRecipeCategory(RecipeCategory)
   */
  Object2ObjectMap<@NotNull Item, @Nullable RecipeCategory> ITEM_TO_RECIPE_CATEGORY = new Object2ObjectOpenHashMap<>();

  /**
   * Query the id of the item. You have to <em>override</em> this method if your class that implements this method is not a subtype of {@link Item}.
   *
   * @return The id of the item.
   */
  @Contract(pure = true)
  default Identifier getItemId() {
    return Registries.ITEM.getId((Item) this);
  }


  // CLIENT PART
  // It's recommended to annotate @Environment(EnvType.CLIENT) when overriding following methods.

  /**
   * The id of the model of its block item. It is usually <code><var>namespace</var>:item/<var>path</var></code>.
   *
   * @return The id of the item model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default Identifier getItemModelId() {
    return getItemId().brrp_prefixed("item/");
  }

  /**
   * The texture of the item. It is usually the format of <code><var>namespace</var>:item/<var>path</var></code>, which <em>mostly</em> equals to the item id. This is mainly used in {@link #getItemModel()}, but you can also bypass this method when overriding it.
   *
   * @return The id of the item texture.
   * @see BlockResourceGenerator#getTextureId(net.minecraft.data.client.TextureKey)
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default Identifier getTextureId() {
    return getItemId().brrp_prefixed("item/");
  }

  /**
   * The model of the item. If you do not need an item model in the runtime resource pack, you can override this method and make it return {@code null}.
   *
   * @return The item model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default ModelJsonBuilder getItemModel() {
    return new ModelJsonBuilder().parent(Models.GENERATED).addTexture(TextureKey.LAYER0, getTextureId());
  }

  /**
   * Write the item model (returned in {@link #getItemModel}) to the runtime resource pack. It does nothing if the returned model is {@code null}.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeItemModel(RuntimeResourcePack pack) {
    final ModelJsonBuilder model = getItemModel();
    if (model != null) pack.addModel(getItemModelId(), model);
  }

  /**
   * <p>Write client assets of this item. In this case, only item model is written, but you can add more. For example, in {@link BlockResourceGenerator#writeAssets}, the block states definition and block model are also written.</p>
   * <p>It's recommended to restrict the call to this method in client environment, like the follows:</p>
   * <pre>{@code
   * if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
   *   writeAssets(pack);
   * }
   * }</pre>
   *
   * @param pack The runtime resource pack.
   * @see #writeData(RuntimeResourcePack)
   * @see #writeAssets(RuntimeResourcePack)
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeAssets(RuntimeResourcePack pack) {
    writeItemModel(pack);
  }


  // SERVER PART
  // Please do not annotate these methods with @Environment when overriding, unless you're sure to do that.

  /**
   * @return The crafting recipe of this item.
   */
  @Contract(pure = true)
  default CraftingRecipeJsonBuilder getCraftingRecipe() {
    return null;
  }

  /**
   * Get the recipe category of this item. You can just override this method so that you do not need to modify {@link #ITEM_TO_RECIPE_CATEGORY}. The return value is by default not null, but may be null if you have invoked {@link #setRecipeCategory(RecipeCategory)} with a null value.
   *
   * @return The recipe category of this item. It may be used in {@link #getCraftingRecipe()}.
   */
  @Contract(pure = true)
  default RecipeCategory getRecipeCategory() {
    if (this instanceof ItemConvertible itemConvertible) {
      return ITEM_TO_RECIPE_CATEGORY.getOrDefault(itemConvertible.asItem(), RecipeCategory.MISC);
    } else {
      return RecipeCategory.MISC;
    }
  }

  /**
   * Set the recipe category of this item. It will directly modify {@link #ITEM_TO_RECIPE_CATEGORY} which may be used in {@link #getRecipeCategory()}. You may also directly override {@link #getRecipeCategory()} to modify the recipe category.
   * <p>
   * Note that this method is only used for data generation, so you should invoke it before the recipe is generated.
   *
   * @param recipeCategory The recipe category.
   */
  default void setRecipeCategory(@Nullable RecipeCategory recipeCategory) {
    if (this instanceof ItemConvertible itemConvertible) {
      ITEM_TO_RECIPE_CATEGORY.put(itemConvertible.asItem(), recipeCategory);
    } else {
      throw new UnsupportedOperationException("Cannot invoke setRecipeCategory for non-ItemConvertible objects. Maybe you have to override this method.");
    }
  }

  /**
   * <p>Get the identifier of its recipe. It is usually the same of the item id.</p>
   * <p>It can be the id for any form of recipe: crafting, smelting, stonecutting, etc. If an item has multiple recipes to make, different ids are distinguished by suffix. For example, a blackstone stairs block can either be crafted or be stone-cut; the crafting recipe id is <span">{@code minecraft:blackstone_stairs}</span> and the stonecutting id is <span>{@code minecraft:blackstone_stairs_from_stonecutting}</span>.</p>
   *
   * @return The id of the recipe.
   */
  @Contract(pure = true)
  default Identifier getRecipeId() {
    return getItemId();
  }

  /**
   * <p>Write the recipes to the runtime resource pack. By default, it has only crafting recipes, but you can add more recipes.</p>
   * <p>When writing recipes, the corresponding advancement of the recipe will be written as well, as long as the advancement is not null and not empty.</p>
   *
   * @param pack The runtime resource pack.
   */
  default void writeRecipes(RuntimeResourcePack pack) {
    final @Nullable CraftingRecipeJsonBuilder recipe = getCraftingRecipe();
    if (recipe != null) {
      final Identifier recipeId = getRecipeId();
      pack.addRecipeAndAdvancement(recipeId, recipe);
    }
  }

  /**
   * Write all server data of the item to the runtime resource pack. In this case, only recipe is used, but you can add more. For example, in {@link BlockResourceGenerator#writeData}, the block loot table is also written.
   *
   * @param pack The runtime resource pack.
   * @see #writeAssets(RuntimeResourcePack)
   * @see #writeData(RuntimeResourcePack)
   */
  default void writeData(RuntimeResourcePack pack) {
    writeRecipes(pack);
  }

  /**
   * Write client assets if the instance is in client environment, and write server data in both environments. It simply calls {@link #writeAssets} and {@link #writeData}. It's not recommended to override this method.
   *
   * @param pack The runtime resource pack.
   * @see #writeAssets(RuntimeResourcePack)
   * @see #writeData(RuntimeResourcePack)
   */
  default void writeAll(RuntimeResourcePack pack) {
    if (PlatformBridge.getInstance().isClientEnvironment()) {
      writeAssets(pack);
    }
    writeData(pack);
  }

  /**
   * Write resources in the specified environment. It's not recommended to override this method.
   *
   * @param pack         The runtime resource pack.
   * @param resourceType The resource type to write. If it is null, both resource types will be used, regardless of the instance environment.
   */
  @ApiStatus.NonExtendable
  default void writeResources(RuntimeResourcePack pack, @Nullable ResourceType resourceType) {
    if (resourceType == null) {
      writeAssets(pack);
      writeData(pack);
    } else if (resourceType == ResourceType.CLIENT_RESOURCES) {
      writeAssets(pack);
    } else {
      writeData(pack);
    }
  }

  /**
   * Write resources in the specified environments. It's not recommended to override this method.
   *
   * @param pack           The runtime resource pack.
   * @param clientIncluded Whether to write client resources to this object.
   * @param serverIncluded Whether to write server data to this object.
   */
  @ApiStatus.NonExtendable
  default void writeResources(RuntimeResourcePack pack, boolean clientIncluded, boolean serverIncluded) {
    if (clientIncluded) {
      writeAssets(pack);
    }
    if (serverIncluded) {
      writeData(pack);
    }
  }
}
