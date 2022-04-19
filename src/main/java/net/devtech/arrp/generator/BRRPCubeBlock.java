package net.devtech.arrp.generator;

import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a simple extension of a cube block. You can specify textures for it.
 */
public class BRRPCubeBlock extends Block implements BlockResourceGenerator {
  public final String parent;
  public final JTextures textures;

  /**
   * This instance can provide models through these fields.
   *
   * @param settings The block settings.
   * @param parent   The id of the parent model. For example, {@code "block/cube_all"}.
   * @param textures Texture definitions of the model.
   */
  @ApiStatus.Internal
  public BRRPCubeBlock(Settings settings, String parent, JTextures textures) {
    super(settings);
    this.parent = parent;
    this.textures = textures;
  }

  /**
   * The block will use the "allTexture" for all sides. It's a classic "cube_all".
   *
   * @param settings   The block settings.
   * @param allTexture Texture for all sides.
   * @return A new instance.
   */
  public static BRRPCubeBlock cubeAll(Settings settings, String allTexture) {
    return new BRRPCubeBlock(settings, "block/cube_all", JTextures.ofAll(allTexture));
  }

  /**
   * The block is a simple "cube_bottom_top".
   *
   * @param settings      The block settings.
   * @param topTexture    Texture for top side.
   * @param sideTexture   Texture for horizontal sides.
   * @param bottomTexture Texture for bottom side.
   * @return A new instance.
   */
  public static BRRPCubeBlock cubeBottomTop(Settings settings, String topTexture, String sideTexture, String bottomTexture) {
    return new BRRPCubeBlock(settings, "block/cube_bottom_top", JTextures.ofSides(topTexture, sideTexture, bottomTexture));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JBlockStates getBlockStates() {
    return JBlockStates.simple(getBlockModelId());
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel(parent).textures(textures);
  }


  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull String getTextureId(@Nullable String type) {
    final String texture = TextureRegistry.getTexture(this, type);
    if (texture != null) return texture;
    return textures.getOrDefault(type, textures.getOrDefault("side", textures.getOrDefault("all", BlockResourceGenerator.super.getTextureId(type))));
  }
}
