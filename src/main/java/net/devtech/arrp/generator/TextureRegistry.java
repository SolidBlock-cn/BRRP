package net.devtech.arrp.generator;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This is used for overriding textures. Sometimes you created a block, and wants the {@link BlockResourceGenerator#getTextureId} use textures other than the block id. You do not need to override the method, creating an anonymous subclass. You can directly use this registry to specify the textures used.</p>
 * <p>Notice that the texture registry is <i>not always</i> respected when getting the texture. For example, in {@link SmartCubeBlock}, the texture definitions may be predetermined when instantiating. That will ignore this registry, even if manually calling {@link BlockResourceGenerator#getTextureId} is still affected by the registry.</p>
 * <p>Block-based blocks, for example, slabs, may query the texture registry for their base blocks, as defined in {@link SmartSlabBlock#getTextureId}. This does not affect vanilla blocks.</p>
 * <p>The texture registry consists of two parts: general and custom. You can specify a custom type, which can be {@code "top"}, {@code "bottom"}, or anything. When querying a type does not exists, it fall backs to general texture registry.</p>
 * <p>For example, if you register a sandstone block like this:</p>
 * <pre>{@code
 * TextureRegistry.register(Blocks.SANDSTONE, "top", "minecraft:block/sandstone_top");
 * TextureRegistry.register(Blocks.SANDSTONE, "bottom", "minecraft:block/sandstone_bottom");
 *
 * TextureRegistry.register(Blocks.SMOOTH_SANDSTONE, "minecraft:block/sandstone_top");
 * }</pre>
 * <p>then:</p>
 * <blockquote>
 * {@code TextureRegistry.getTexture(Blocks.SANDSTONE, "top")} returns {@code "minecraft:block/sandstone_top"}<p>
 * {@code TextureRegistry.getTexture(Blocks.SANDSTONE)} returns {@code null}<p>
 * And {@code ((BlockResourceGenerator) Blocks.SANDSTONE).getTextureId(null)} may return {@code "minecraft:block/sandstone"}, as the default behaviour.<p>
 * {@code TextureRegistry.getTexture(Blocks.SMOOTH_SANDSTONE)} returns {@code "minecraft:block/sandstone_top"}, as registered above, and
 * {@code TextureRegistry.getTexture(Blocks.SMOOTH_SANDSTONE, "top")} also returns {@code "minecraft:block/sandstone_top"}, because it fall backs to general registry.
 * </blockquote>
 * <p>You may also notice that the block as keys is regarded "reference" instead of "object", which means it determines equal through the "{@code ==}" operator, instead of {@code equals} method.</p>
 */
public final class TextureRegistry {
  /**
   * Keys are blocks, and values are texture ids (as string). When querying a custom registry and not found, this map will be used as a "fallback".
   */
  private static final Reference2ObjectMap<Block, String> GENERAL_TEXTURES = new Reference2ObjectOpenHashMap<>();
  /**
   * Keys are blocks, and keys of values are texture types (as string), and values are texture ids (as string). When querying a general texture registry, this map will not be used.
   */
  private static final Reference2ObjectMap<Block, Map<String, String>> CUSTOM_TEXTURES = new Reference2ObjectOpenHashMap<>();

  /**
   * Register a general block texture. The texture will be used when generating models through {@link BlockResourceGenerator#getBlockModel()}. And when querying a custom model that does not exist for the specified type, this will also be used as a fallback.
   *
   * @param block   The block you register.
   * @param texture The id (as string) of the texture that will be used.
   */
  public static void register(Block block, String texture) {
    GENERAL_TEXTURES.put(block, texture);
  }

  /**
   * Register a custom block texture in the specified type.
   *
   * @param block   The block you register.
   * @param type    The type of the texture, usually indicated which part of the block. It can be usually {@code "top"}, {@code "bottom"}, or something like.
   * @param texture The id (as string) of the texture that will be used.
   */
  public static void register(Block block, String type, String texture) {
    if (!CUSTOM_TEXTURES.containsKey(block)) CUSTOM_TEXTURES.put(block, new HashMap<>());
    CUSTOM_TEXTURES.get(block).put(type, texture);
  }

  /**
   * Query the general texture registry. In this case, custom texture registry will not be used.
   *
   * @param block The block. Note that it mused be the same as registered ("{@code ==}"), instead of merely "{@link Object#equals}".
   * @return The id (as string) of the texture you registered.
   */
  public static String getTexture(Block block) {
    return GENERAL_TEXTURES.get(block);
  }

  /**
   * Query a custom texture registry in the specified type. If the custom texture registry is not found, then the general texture registry will be queried.
   *
   * @param block The block. Note that it mused be the same as registered ("{@code ==}"), instead of merely "{@link Object#equals}".
   * @param type  The type of texture, usually indicated which part of the block. If it's {@code null}, then it directly queries the general registry.
   * @return The id (as string) of the texture you registered in custom or general registry.
   */
  public static String getTexture(Block block, @Nullable String type) {
    if (type == null) return GENERAL_TEXTURES.get(block);
    else return CUSTOM_TEXTURES.getOrDefault(block, Collections.emptyMap()).getOrDefault(type, GENERAL_TEXTURES.get(block));
  }
}
