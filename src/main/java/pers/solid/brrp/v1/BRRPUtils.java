package pers.solid.brrp.v1;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;
import pers.solid.brrp.v1.generator.BlockResourceGenerator;
import pers.solid.brrp.v1.generator.ItemResourceGenerator;
import pers.solid.brrp.v1.generator.TextureRegistry;

import java.util.function.BiFunction;

/**
 * <p>It's similar to {@link ItemResourceGenerator} and {@link BlockResourceGenerator}, but it provides more <em>static</em> methods so that they can be used for blocks and items that do not implement {@link ItemResourceGenerator}, which are usually vanilla blocks.</p>
 * <p>If your sure that the instance <em>is</em> a {@link ItemResourceGenerator}, you can use their methods instead, instead of these static methods. Also, notice that these methods may not reflect the actual usage. For example, {@link #getTextureId(Block, TextureKey)} just respects the {@link TextureRegistry} in this mod, or overrides to {@link BlockResourceGenerator#getTextureId(TextureKey)}, instead of what is actually used.</p>
 *
 * @author SolidBlock
 */
public final class BRRPUtils {
  private BRRPUtils() {
  }

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
   * Get the identifier of the item model, which is usually in the format of <code><var>namespace</var>:item/<var>path</var></code>. It works for items that do not implement {@link ItemResourceGenerator}, but may fail to be accurate in cases other mods modify the item model (which are not usually supposed to happen).
   *
   * @return The id of the item model.
   * @see net.minecraft.data.client.ModelIds#getItemModelId(Item)
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getItemModelId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : ModelIds.getItemModelId(item.asItem());
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
   * Get the identifier of the block model, which is usually in the format of <code><var>namespace</var>:block/<var>path</var></code>. It works for blocks that do not implement {@link BlockResourceGenerator}, but may fail to be accurate in cases other mods modify the block model (which are not usually supposed to happen), or the model is already not in other formats (possibly specified in the block states).
   *
   * @return The id of the block model.
   * @see ModelIds#getBlockModelId(Block)
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : ModelIds.getBlockModelId(block);
  }

  /**
   * Get the texture of a block with a specified texture key, which respects {@link TextureRegistry}. Note that <em>it may not correctly return the real texture used</em>, as they are specified in block models, instead of {@code TextureRegistry}. The method <strong>only</strong> respects {@link TextureRegistry}.
   *
   * @return The texture id of the block specified in {@link BlockResourceGenerator#getTextureId} or in the {@link TextureRegistry}.
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getTextureId(@NotNull Block block, @NotNull TextureKey textureKey) {
    if (block instanceof BlockResourceGenerator generator) {
      return generator.getTextureId(textureKey);
    }
    final Identifier texture = TextureRegistry.getTexture(block, textureKey);
    if (texture != null) return texture;
    return getBlockId(block).brrp_prefixed("block/");
  }

  /**
   * Get the id of the ordinary recipe. This method does not check whether the recipe really exists, so the method should be avoided.
   *
   * @return The id of the ordinary recipe.
   */
  public static Identifier getRecipeId(@NotNull ItemConvertible item) {
    if (item instanceof ItemResourceGenerator generator) {
      return generator.getRecipeId();
    } else {
      return getItemId(item);
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

  public static <B extends Block & BlockResourceGenerator> MapCodec<B> createCodecWithBaseBlock(RecordCodecBuilder<B, AbstractBlock.Settings> settingsCodec, BiFunction<Block, AbstractBlock.Settings, B> function) {
    return RecordCodecBuilder.mapCodec(instance -> instance.group(Block.CODEC.fieldOf("base_block").forGetter(BlockResourceGenerator::getBaseBlock), settingsCodec).apply(instance, function));
  }
}
