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
  public static Identifier getItemId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemId() : Registries.ITEM.getId(item.asItem());
  }

  /**
   * @since 0.6.2 Fixed the issue that the item model id is not correct.
   */
  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : Registries.ITEM.getId(item.asItem()).brrp_prepend("item/");
  }

  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockId() : Registries.BLOCK.getId(block);
  }

  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : Registries.BLOCK.getId(block).brrp_prepend("block/");
  }

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
   * Get the id of the ordinary recipe. This method does not check whether the recipe really exists.
   */
  @ApiStatus.AvailableSince("0.6.2")
  public static Identifier getRecipeId(@NotNull ItemConvertible item) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getRecipeId();
    } else {
      return getItemId(item);
    }
  }

  @Contract(pure = true)
  @ApiStatus.AvailableSince("0.8.1")
  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId, @NotNull JRecipe recipe) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getAdvancementIdForRecipe(recipeId, recipe);
    } else {
      if (recipe.recipeCategory != null) {
        return recipeId.brrp_prepend("recipes/" + recipe.recipeCategory.getName() + "/");
      }
      return recipeId;
    }
  }

  @Contract(pure = true)
  @ApiStatus.AvailableSince("0.8.1")
  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId, @Nullable RecipeCategory recipeCategory) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getAdvancementIdForRecipe(recipeId, recipeCategory);
    } else {
      if (recipeCategory != null) {
        return recipeId.brrp_prepend("recipes/" + recipeCategory.getName() + "/");
      }
      return recipeId;
    }
  }

  public static Identifier getLootTableId(@NotNull AbstractBlock block) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getLootTableId();
    } else {
      return block.getLootTableId();
    }
  }
}
