package net.devtech.arrp.generator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>It's similar to {@link ItemResourceGenerator} and {@link BlockResourceGenerator}, but it provides more <i>static</i> methods so that they can be used for blocks and items that do not implement {@link ItemResourceGenerator}.</p>
 * <p>It's not recommended to inject the interfaces above to vanilla classes.</p>
 * <p>If your sure that the instance <i>is</i> a {@link ItemResourceGenerator}, you should use their methods instead, instead of these static methods.</p>
 */
public final class ResourceGeneratorHelper {
  public static Identifier getItemId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemId() : Registry.ITEM.getId(item.asItem());
  }

  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : Registry.ITEM.getId(item.asItem()).brrp_prepend("block/");
  }

  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockId() : Registry.BLOCK.getId(block);
  }

  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : Registry.BLOCK.getId(block).brrp_prepend("block/");
  }

  public static String getTextureId(@NotNull Block block, @Nullable String type) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getTextureId(type);
    }
    final String texture = TextureRegistry.getTexture(block, type);
    if (texture != null) return texture;
    return getBlockId(block).brrp_prepend("block/").toString();
  }

  public static Identifier getLootTableId(@NotNull AbstractBlock block) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getLootTableId();
    } else {
      return block.getLootTableId();
    }
  }
}
