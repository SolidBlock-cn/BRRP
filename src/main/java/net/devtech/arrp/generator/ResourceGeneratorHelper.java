package net.devtech.arrp.generator;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.json.recipe.JRecipe;
import net.fabricmc.api.EnvType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>It's similar to {@link ItemResourceGenerator} and {@link BlockResourceGenerator}, but it provides more <i>static</i> methods so that they can be used for blocks and items that do not implement {@link ItemResourceGenerator}, which are usually vanilla blocks.</p>
 * <p>It's not recommended to inject the interfaces above to vanilla classes using mixins.</p>
 * <p>If your sure that the instance <i>is</i> a {@link ItemResourceGenerator}, you should use their methods instead, instead of these static methods.</p>
 *
 * @author SolidBlock
 */
public final class ResourceGeneratorHelper {
  /**
   * Get the identifier of the item. In most cases you may directly invoke {@link net.minecraft.registry.Registry#getId(Object)} instead, as {@link ItemResourceGenerator#getItemId()} should usually not be overridden.
   *
   * @return The item id.
   */
  @Contract(pure = true)
  public static Identifier getItemId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemId() : Registries.ITEM.getId(item.asItem());
  }

  /**
   * Get the identifier of the item model, which is usually in the format of <code><i>namespace</i>:item/<i>path</i></code>. It works for items that do not implement {@link ItemResourceGenerator}, but may fail to be accurate in cases other mods modify the item model (which are not usually supposed to happen).
   *
   * @return The id of the item model.
   * @since 0.6.2 Fixed the issue that the item model id is not correct.
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : Registries.ITEM.getId(item.asItem()).brrp_prepend("item/");
  }

  /**
   * Get the identifier of the block. In most cases you may directly invoke {@link net.minecraft.registry.Registry#getId(Object)} instead, as {@link BlockResourceGenerator#getBlockId()} should usually not be overridden.
   *
   * @return The item id.
   */
  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockId() : Registries.BLOCK.getId(block);
  }

  /**
   * Get the identifier of the block model, which is usually in the format of <code><i>namespace</i>:block/<i>path</i></code>. It works for blocks that do not implement {@link BlockResourceGenerator}, but may fail to be accurate in cases other mods modify the block model (which are not usually supposed to happen), or the model is already not in other formats (possibly specified in the block states).
   *
   * @return The id of the block model.
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : Registries.BLOCK.getId(block).brrp_prepend("block/");
  }

  /**
   * Get the texture of a block with a specified texture key, which respects {@link TextureRegistry}. Note that <em>it may not correctly return the real texture used</em>, as they are specified in block models, instead of {@code TextureRegistry}. The method <b>only</b> respects {@link TextureRegistry}.
   *
   * @return The texture id of the block specified in {@link BlockResourceGenerator#getTextureId} or in the {@link TextureRegistry}.
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static String getTextureId(@NotNull Block block, @NotNull TextureKey textureKey) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getTextureId(textureKey);
    }
    final Identifier texture = TextureRegistry.getTexture(block, textureKey);
    if (texture != null) return texture.toString();
    return getBlockId(block).brrp_prepend("block/").toString();
  }

  /**
   * Get the id of the ordinary recipe. This method does not check whether the recipe really exists, so the method should be avoided.
   *
   * @return The id of the ordinary recipe.
   */
  @ApiStatus.AvailableSince("0.6.2")
  public static Identifier getRecipeId(@NotNull ItemConvertible item) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getRecipeId();
    } else {
      return getItemId(item);
    }
  }

  /**
   * Get the id of the advancement that corresponds to the recipe. The recipe category in the {@code recipe} parameter will be used.
   *
   * @return The id of the advancement that corresponds to the recipe.
   */
  @Contract(pure = true)
  @ApiStatus.AvailableSince("0.8.1")
  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId, @NotNull JRecipe recipe) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getAdvancementIdForRecipe(recipeId, recipe);
    } else {
      if (recipe.recipeCategory != null) {
        return recipeId.brrp_prepend("recipes/" + recipe.recipeCategory.getName() + "/");
      }
      return recipeId.brrp_prepend("recipes/");
    }
  }

  /**
   * Get the id of the advancement that corresponds to the recipe. The recipe category will be used to be prepended to the recipe id.
   *
   * @return The id of the advancement that corresponds to the recipe.
   */
  @Contract(pure = true)
  @ApiStatus.AvailableSince("0.8.1")
  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId, @Nullable RecipeCategory recipeCategory) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getAdvancementIdForRecipe(recipeId, recipeCategory);
    } else {
      if (recipeCategory != null) {
        return recipeId.brrp_prepend("recipes/" + recipeCategory.getName() + "/");
      }
      return recipeId.brrp_prepend("recipes/");
    }
  }

  /**
   * Get the id of the block loot table, which respects both {@link BlockResourceGenerator#getLootTableId()} and {@link Block#getLootTableId()}. Note that {@link BlockResourceGenerator#getLootTableId()} usually should not be overridden, and {@link Block#getLootTableId()} is a final method.
   *
   * @return The id of the block loot table.
   */
  @Contract(pure = true)
  public static Identifier getLootTableId(@NotNull AbstractBlock block) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getLootTableId();
    } else {
      return block.getLootTableId();
    }
  }
}
