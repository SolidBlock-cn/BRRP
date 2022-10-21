package net.devtech.arrp.generator;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.recipe.JRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * <p>This interface is used for items.</p>
 * <p>Your custom item class can implement this interface, and override some methods you need. You can also implement this interface on your custom class.</p>
 * <p>This interface is divided into three parts:</p>
 * <ul>
 *   <li>general part: to get the identifier of this instance.</li>
 *   <li>client part: methods related to generating and writing client assets. It's <i>highly recommended but not required</i> to annotate the methods as {@code @}{@link net.fabricmc.api.Environment Environment}<code>({@link net.fabricmc.api.EnvType#CLIENT EnvType.CLIENT})</code>, because they are only used in client distribution. When running on a dedicated server, they should be ignored.</li>
 *   <li>server part: methods related to generating and writing server data. Please do not annotate them with {@code @Environment{EnvType.SERVER}}, unless you're sure to do so, as they will be used in client distribution.</li>
 * </ul>
 * <p>Most "get" methods are nullable, which means, when writing (in those "write" methods) the values into the runtime resource pack, these null values will be ignored. When overriding these "get" methods, you can annotate @NotNull if you're sure that the values are not null.</p>
 * <p>To generate the resources to your runtime resource pack, you can call {@link #writeAssets(RuntimeResourcePack)} or {@link #writeData(RuntimeResourcePack)}.</p>
 */
@SuppressWarnings("unused")
public interface ItemResourceGenerator {
  /**
   * Query the id of the item. You <i>override</i> this method if your class that implements this method is not a subtype of {@link Item}.
   *
   * @return The id of the item.
   */
  @Contract(pure = true)
  default Identifier getItemId() {
    return Registry.ITEM.getId((Item) this);
  }


  // CLIENT PART
  // It's recommended to annotate @Environment(EnvType.CLIENT) when overriding following methods.

  /**
   * The id of the model of its block item. It is usually {@code <i>namespace</i>:item/<i>path</i>}.
   *
   * @return The id of the item model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default Identifier getItemModelId() {
    return getItemId().brrp_prepend("item/");
  }

  /**
   * The texture of the item. It is usually the format of <code><i>namespace</i>:item/<i>path</i></code>, which <i>mostly</i> equals to the item id. This is mainly used in {@link #getItemModel()}, but you can also bypass this method when overriding it.
   *
   * @return The id of the item texture.
   * @see BlockResourceGenerator#getTextureId(net.minecraft.data.client.TextureKey)
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default String getTextureId() {
    return getItemId().brrp_prepend("item/").toString();
  }

  /**
   * The model of the item. If you do not need an item model in the runtime resource pack, you can override this method and make it return {@code null}.
   *
   * @return The item model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default @Nullable JModel getItemModel() {
    return new JModel("item/generated").textures(new JTextures().layer0(getTextureId()));
  }

  /**
   * Write the item model (returned in {@link #getItemModel}) to the runtime resource pack. It does nothing if the returned model is {@code null}.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(mutates = "param1")
  default void writeItemModel(RuntimeResourcePack pack) {
    final JModel model = getItemModel();
    if (model != null) pack.addModel(model, getItemModelId());
  }

  /**
   * Write client assets of this item. In this case, only item model is written, but you can add more. For example, in {@link BlockResourceGenerator#writeAssets}, the block states definition and block model are also written.<br>
   * It's recommended to restrict the call to this method in client environment, like the follows:
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
  @Contract(mutates = "param1")
  default void writeAssets(RuntimeResourcePack pack) {
    writeItemModel(pack);
  }


  // SERVER PART
  // Please do not annotate these methods with @Environment when overriding, unless you're sure to do that.

  /**
   * @return The crafting recipe of this item.
   */
  @Contract(pure = true)
  default @Nullable JRecipe getCraftingRecipe() {
    return null;
  }

  /**
   * <p>Get the identifier of its recipe file. It is usually the same of the item id.</p>
   * <p>It can be the id for any form of recipe: crafting, smelting, stonecutting, etc. If an item has multiple recipes to make, different ids are distinguished by suffix. For example, a blackstone stairs block can either be crafted or be stone-cut; the crafting recipe id is <span style="color:maroon">{@code minecraft:blackstone_stairs}</span> and the stonecutting id is <span style="color:maroon">{@code minecraft:blackstone_stairs_from_stonecutting}</span>.</p>
   *
   * @return The id of the recipe.
   */
  @Contract(pure = true)
  default Identifier getRecipeId() {
    return getItemId();
  }

  /**
   * <p>Get the identifier of the advancement that corresponds to the recipe. It is usually in the format of <code style=color:maroon><i>namespace</i>:recipes/<i>group</i>/<i>path</i></code>. For example, the advancement id that corresponds to the recipe id for acacia stairs can be <code style=color:maroon>minecraft:recipe/building_blocks/acacia_stairs</code>.</p>
   * <p>In this method, the recipe id you input will be appended with {@code "recipes/"} and the item group name, if there is one.</p>
   *
   * @return The id of the advancement that corresponds to its recipe.
   */
  @ApiStatus.AvailableSince("0.6.2")
  @Contract(pure = true)
  default Identifier getAdvancementIdForRecipe(Identifier recipeId) {
    if (this instanceof ItemConvertible itemConvertible) {
      final ItemGroup group = itemConvertible.asItem().getGroup();
      if (group != null) {
        return recipeId.brrp_prepend("recipes/" + group.getName() + "/");
      }
    }
    return getItemId().brrp_prepend("recipes/");
  }

  /**
   * <p>Write the recipes to the runtime resource pack. By default, it has only crafting recipes, but you can add more recipes.</p>
   * <p>When writing recipes, the corresponding advancement of the recipe will be written as well, as long as the advancement is not null and not empty.</p>
   *
   * @param pack The runtime resource pack.
   */
  @Contract(mutates = "param1")
  default void writeRecipes(RuntimeResourcePack pack) {
    final JRecipe craftingRecipe = getCraftingRecipe();
    if (craftingRecipe != null) {
      final Identifier recipeId = getRecipeId();
      pack.addRecipe(recipeId, craftingRecipe);
      pack.addRecipeAdvancement(recipeId, getAdvancementIdForRecipe(recipeId), craftingRecipe);
    }
  }

  /**
   * Write server data to the runtime resource pack. In this case, only recipe is used, but you can add more. For example, in {@link BlockResourceGenerator#writeData}, the block loot table is also written.
   *
   * @param pack The runtime resource pack.
   * @see #writeAssets(RuntimeResourcePack)
   * @see #writeData(RuntimeResourcePack)
   */
  @Contract(mutates = "param1")
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
  @Contract(mutates = "param1")
  default void writeAll(RuntimeResourcePack pack) {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      writeAssets(pack);
    }
    writeData(pack);
  }

  /**
   * Write resources in the specified environment. It is usually used for {@link net.devtech.arrp.api.RRPCallbackConditional}. It's not recommended to override this method.
   *
   * @param pack         The runtime resource pack.
   * @param resourceType The resource type to write. If it is null, both resource types will be used, regardless of the instance environment.
   */
  @Contract(mutates = "param1")
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
}
