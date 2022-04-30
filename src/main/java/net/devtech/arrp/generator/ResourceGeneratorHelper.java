package net.devtech.arrp.generator;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <p>It's similar to {@link ItemResourceGenerator} and {@link BlockResourceGenerator}, but it provides more <i>static</i> methods so that they can be used for blocks and items that do not implement {@link ItemResourceGenerator}, which are usually vanilla blocks.</p>
 * <p>It's not recommended to inject the interfaces above to vanilla classes using mixins.</p>
 * <p>If your sure that the instance <i>is</i> a {@link ItemResourceGenerator}, you should use their methods instead, instead of these static methods.</p>
 *
 * @author SolidBlock
 */
public final class ResourceGeneratorHelper {
  public static Identifier getItemId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator) ? ((ItemResourceGenerator) item).getItemId() : Registry.ITEM.getId(item.asItem());
  }

  /**
   * @since 0.6.2 Fixed the issue that the item model id is not correct.
   */
  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator) ? ((ItemResourceGenerator) item).getItemModelId() : Registry.ITEM.getId(item.asItem()).brrp_prepend("item/");
  }

  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator) ? ((BlockResourceGenerator) block).getBlockId() : Registry.BLOCK.getId(block);
  }

  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator) ? ((BlockResourceGenerator) block).getBlockModelId() : Registry.BLOCK.getId(block).brrp_prepend("block/");
  }

  @PreferredEnvironment(EnvType.CLIENT)
  public static String getTextureId(@NotNull Block block, @NotNull TextureKey textureKey) {
    if (block instanceof BlockResourceGenerator) {
      return ((BlockResourceGenerator) block).getTextureId(textureKey);
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
    if (item instanceof ItemResourceGenerator) {
      return ((ItemResourceGenerator) item).getRecipeId();
    } else {
      return getItemId(item);
    }
  }

  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId) {
    if (item instanceof ItemResourceGenerator) {
      return ((ItemResourceGenerator) item).getAdvancementIdForRecipe(recipeId);
    } else {
      final ItemGroup group = item.asItem().getGroup();
      if (group != null) {
        return recipeId.brrp_prepend("recipes/" + group.getName() + "/");
      }
      return getItemId(item).brrp_prepend("recipes/");
    }
  }

  public static Identifier getLootTableId(@NotNull AbstractBlock block) {
    if (block instanceof BlockResourceGenerator) {
      return block.getLootTableId();
    } else {
      return block.getLootTableId();
    }
  }
}
