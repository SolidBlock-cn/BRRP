package pers.solid.brrp.v1.generator;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;

/**
 * <p>This is used for recording textures. Sometimes you created a block, and wants the {@link BlockResourceGenerator#getTextureId} to use textures that differ from the block id. You do not need to override the method. You can directly use this registry to specify the textures used.</p>
 * <p>Notice that the texture registry is <em>not always</em> respected when getting the texture. For example, in {@link BRRPCubeBlock}, the texture definitions may be predetermined when instantiating. That may ignore this registry.</p>
 * <p>Block-based blocks, for example, slabs, may query the texture registry for their base blocks, as defined in, for example, {@link BRRPSlabBlock#getTextureId}. This affects all blocks that override {@link BlockResourceGenerator#getBaseBlock()}, but <em>does not affect vanilla blocks</em> because they do not implement that method.</p>
 * <p>The texture key respects their parent keys (for "fallback keys"). For example, {@link TextureKey#EAST} fall backs to {@link TextureKey#SIDE}, which fall backs to {@link TextureKey#ALL}, so when querying the east texture of the block, and that texture key of the block is not registered, side texture will be used; if side texture does not exist as well, its all texture will be used, and if that is still absent, {@code null} value will be returned.</p>
 * <p>To register, you can simple call {@link #register}, and you can call {@link #getTexture} to get the texture.</p>
 * <p><em>Pay attention that textures of vanilla blocks are not registered in the registry</em> by default. <strong>If I want to create variant blocks for vanilla blocks, can I register textures for vanilla blocks?</strong> Sure. It's OK to register vanilla blocks, but you may have to consider compatibility with other mods. If you're willing to register vanilla block textures to this registry (such as Extended Block Shapes mod), it's highly recommended to keep it identical to what the vanilla textures use (see {@link net.minecraft.data.client.BlockStateModelGenerator}).</p>
 * <p>For example, if you register a sandstone block like this:</p>
 * <pre>{@code
 * TextureRegistry.registerAppended(Blocks.SANDSTONE, TextureKey.TOP, "_top");
 * TextureRegistry.registerAppended(Blocks.SANDSTONE, TextureKey.BOTTOM, "_bottom");
 *
 * TextureRegistry.registerWithName(Blocks.SMOOTH_SANDSTONE, "sandstone_top");
 * }</pre>
 * <p>then:</p>
 * <ul>
 * <li>{@code TextureRegistry.getTexture(Blocks.SANDSTONE, TextureKey.TOP)} returns <code>minecraft:block/sandstone_top</code>.</li><li>
 * {@code TextureRegistry.getTexture(Blocks.SANDSTONE)} returns {@code null}.</li><li>
 * And {@code ResourceGeneratorHelper.getTextureId(Blocks.SANDSTONE, null)} may return <code>minecraft:block/sandstone</code>, as the default behaviour.</li><li>
 * {@code TextureRegistry.getTexture(Blocks.SMOOTH_SANDSTONE)} returns <code>minecraft:block/sandstone_top</code>, as registered above, and</li><li>
 * {@code TextureRegistry.getTexture(Blocks.SMOOTH_SANDSTONE, TextureKey.TOP)} also returns <code>minecraft:block/sandstone_top</code>, because it fall backs to general registry.</li>
 * </ul>
 */
public final class TextureRegistry {
  /**
   * The map storing blocks and their texture maps. Keys are blocks, and values are their texture maps.
   */
  private static final Object2ObjectMap<@NotNull Block, TextureMap> TEXTURE_MAPS = new Object2ObjectOpenHashMap<>();

  private TextureRegistry() {
  }

  /**
   * <p>Register a block texture with a default texture key ({@link TextureKey#ALL}). The texture key {@code TextureKey.ALL} is the fallback texture key of most texture keys, so it is regarded default.</p>
   * <p>If there is no texture map that corresponds to the block, the map will be created and put with the default texture key and texture.</p>
   *
   * @param block   The block you register.
   * @param texture The identifier of the texture. It is usually in the form of <code><var>namespace:</var>block/<em>path</em></code>
   */
  public static void register(Block block, Identifier texture) {
    register(block, TextureKey.ALL, texture);
  }

  /**
   * <p>Register a block texture with the specified texture key.</p>
   * <p>If there is no texture map that corresponds to the block, the map will be created and put with the texture key and texture.</p>
   *
   * @param block      The block you register.
   * @param textureKey The texture key. Vanilla texture keys can be found in {@link TextureKey}.
   * @param texture    The identifier of the texture. It is usually in the form of <code><var>namespace:</var>block/<em>path</em></code>
   */
  public static void register(Block block, TextureKey textureKey, Identifier texture) {
    TEXTURE_MAPS.computeIfAbsent(block, b -> new TextureMap()).put(textureKey, texture);
  }

  /**
   * <p>Register a block texture with the specified texture key. The identifier is created by the block identifier with the specified suffix.</p>
   * <p>In this case, the block id will be used, so please make sure that the block has been registered in {@link Registries#BLOCK}. This is a convenient way.</p>
   * <p>The block with id {@code minecraft:sandstone} with the suffix {@code "_top"} parameter will use the following texture identifier: {@code minecraft:block/sandstone_top}.</p>
   *
   * @param block      The block you register.
   * @param textureKey The texture key. Vanilla texture keys can be found in {@link TextureKey}.
   * @param suffix     The suffix to append to the block id.
   */
  public static void registerSuffixed(Block block, TextureKey textureKey, String suffix) {
    register(block, textureKey, Registries.BLOCK.getId(block).brrp_prefix_and_suffixed("block/", suffix));
  }

  /**
   * <p>Register a block texture with the specified texture key. The identifier is created with the same namespace of the block id and the specified path.</p>
   * <p>In this case, the namespace of block id will be used, so please make sure that the block has been registered in {@link Registries#BLOCK}.</p>
   * <p>For example, the block with id {@code minecraft:smooth_sandstone} with the path {@code "sandstone_top"} parameter will use the following texture identifier: {@code minecraft:block/sandstone_top}.</p>
   *
   * @param block      The block you register.
   * @param textureKey The texture key. Vanilla texture keys can be found in {@link TextureKey}.
   * @param path       The path of the identifier (not including {@code "blocks/"}).
   */
  public static void registerWithName(Block block, TextureKey textureKey, String path) {
    final Identifier id = Registries.BLOCK.getId(block);
    register(block, textureKey, Identifier.of(id.getNamespace(), "block/" + path));
  }

  /**
   * <p>Get a block texture with the specified texture key. <strong>This method only respects the TextureRegistry.</strong> If the texture map is not found with the block, {@code null} will be returned regardless of the texture key. If the map exists but the texture key does not exist, parent key (or called "fallback key") of that key will be used, and if it still does not exist, {@code null} will be returned.</p>
   *
   * @param block      The block you query.
   * @param textureKey The texture key you query.
   * @return The identifier of the corresponding texture, or {@code null} if the texture does not exist.
   * @see #getTexture(Block)
   * @see TextureMap#getTexture(TextureKey)
   */
  public static @Nullable Identifier getTexture(@NotNull Block block, @NotNull TextureKey textureKey) {
    final @Nullable TextureMap textureMap = TEXTURE_MAPS.getOrDefault(block, null);
    if (textureMap == null) return null;
    try {
      // In textureMap#getTexture, the exception will be thrown if the texture is not found. However, we do not throw any exceptions. We just return null.
      return textureMap.getTexture(textureKey);
    } catch (IllegalStateException e) {
      return null;
    }
  }

  /**
   * Get a block texture with the default texture key. If the texture map is not found with the block, {@code null} with be returned. The default texture key {@link TextureKey#ALL} has no parent key.
   *
   * @param block The block you query.
   * @return The identifier of the corresponding texture, or {@code null} if the texture does not exist.
   * @see #getTexture(Block, TextureKey)
   * @see TextureMap#getTexture(TextureKey)
   */
  public static @Nullable Identifier getTexture(@NotNull Block block) {
    return getTexture(block, TextureKey.ALL);
  }

  /**
   * <p>Get the <em>unmodifiable view</em> of the map storing the blocks and their textures. However, their texture maps are not unmodifiable; they are passes <em>as is</em>.</p>
   * <p>To get the specific texture map, you can, for example:</p>
   * <pre>{@code
   * TextureRegistry.getTextureMap().get(Blocks.STONE)
   * }</pre>
   *
   * @return The unmodifiable view of map of texture map.
   */
  public static @UnmodifiableView Map<Block, TextureMap> getTextureMaps() {
    return Collections.unmodifiableMap(TEXTURE_MAPS);
  }
}
