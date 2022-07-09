package net.devtech.arrp.generator;

import com.google.common.base.Preconditions;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ForgeRegistries;
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
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemId() : ForgeRegistries.ITEMS.getKey(item.asItem());
  }

  /**
   * @since 0.6.2 Fixed the issue that the item model id is not correct.
   */
  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : Preconditions.checkNotNull(ForgeRegistries.ITEMS.getKey(item.asItem()), "The item is not registered.").brrp_prepend("item/");
  }

  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockId() : ForgeRegistries.BLOCKS.getKey(block);
  }

  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : Preconditions.checkNotNull(ForgeRegistries.BLOCKS.getKey(block), "The block is not registered.").brrp_prepend("block/");
  }

  @PreferredEnvironment(Dist.CLIENT)
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

  @ApiStatus.AvailableSince("0.6.2")
  public static Identifier getAdvancementIdForRecipe(@NotNull ItemConvertible item, Identifier recipeId) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getAdvancementIdForRecipe(recipeId);
    } else {
      final ItemGroup group = item.asItem().getGroup();
      if (group != null) {
        return recipeId.brrp_prepend("recipes/" + group.getName() + "/");
      }
      return getItemId(item).brrp_prepend("recipes/");
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
