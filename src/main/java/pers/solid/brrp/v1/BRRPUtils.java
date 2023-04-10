package pers.solid.brrp.v1;

import net.fabricmc.api.EnvType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;
import pers.solid.brrp.v1.generator.BlockResourceGenerator;
import pers.solid.brrp.v1.generator.ItemResourceGenerator;
import pers.solid.brrp.v1.generator.TextureRegistry;

/**
 * <p>It's similar to {@link ItemResourceGenerator} and {@link BlockResourceGenerator}, but it provides more <i>static</i> methods so that they can be used for blocks and items that do not implement {@link ItemResourceGenerator}, which are usually vanilla blocks.</p>
 * <p>It's not recommended to inject the interfaces above to vanilla classes using mixins.</p>
 * <p>If your sure that the instance <i>is</i> a {@link ItemResourceGenerator}, you should use their methods instead, instead of these static methods.</p>
 *
 * @author SolidBlock
 */
public final class BRRPUtils {
  private BRRPUtils() {
  }

  /**
   * Get the identifier of the item. In most cases you may directly invoke {@link Registry#getId(Object)} instead, as {@link ItemResourceGenerator#getItemId()} should usually not be overridden.
   *
   * @return The item id.
   */
  @Contract(pure = true)
  public static Identifier getItemId(@NotNull ItemConvertible item) {
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemId() : Registry.ITEM.getId(item.asItem());
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
    return (item instanceof ItemResourceGenerator generator) ? generator.getItemModelId() : Registry.ITEM.getId(item.asItem()).brrp_prefixed("item/");
  }

  /**
   * Get the identifier of the block. In most cases you may directly invoke {@link Registry#getId(Object)} instead, as {@link BlockResourceGenerator#getBlockId()} should usually not be overridden.
   *
   * @return The item id.
   */
  public static Identifier getBlockId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockId() : Registry.BLOCK.getId(block);
  }

  /**
   * Get the identifier of the block model, which is usually in the format of <code><i>namespace</i>:block/<i>path</i></code>. It works for blocks that do not implement {@link BlockResourceGenerator}, but may fail to be accurate in cases other mods modify the block model (which are not usually supposed to happen), or the model is already not in other formats (possibly specified in the block states).
   *
   * @return The id of the block model.
   */
  @Contract(pure = true)
  @PreferredEnvironment(EnvType.CLIENT)
  public static Identifier getBlockModelId(@NotNull Block block) {
    return (block instanceof BlockResourceGenerator generator) ? generator.getBlockModelId() : Registry.BLOCK.getId(block).brrp_prefixed("block/");
  }

  /**
   * Get the texture of a block with a specified texture key, which respects {@link TextureRegistry}. Note that <em>it may not correctly return the real texture used</em>, as they are specified in block models, instead of {@code TextureRegistry}. The method <b>only</b> respects {@link TextureRegistry}.
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
}
