package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * <p>This is a simple extension of a cube block which implements {@link BlockResourceGenerator}. You can specify textures for it in the constructor, and then you do not need to use {@link TextureRegistry} to specify textures.</p>
 * <p>Please loot at this example:</p>
 * <pre>{@code
 *   // create a block that uses the texture of lava_still
 *   public static final BRRPCubeBlock LAVA_BLOCK = BRRPCubeBlock.cubeAll(Settings.of(...), new Identifier("block/lava_still"));
 * }
 * </pre>
 */
public class BRRPCubeBlock extends Block implements BlockResourceGenerator {
  /**
   * The id of the parent model.
   */
  public final Identifier parent;
  /**
   * The texture map of the block.
   */
  public final TextureMap textures;

  /**
   * This instance can provide models through these fields.
   *
   * @param settings The block settings.
   * @param parent   The id of the parent model. For example, {@code "block/cube_all"}.
   * @param textures Texture definitions of the model.
   */
  public BRRPCubeBlock(Settings settings, Identifier parent, TextureMap textures) {
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
  public static BRRPCubeBlock cubeAll(Settings settings, Identifier allTexture) {
    return new BRRPCubeBlock(settings, ModelUtils.getId(Models.CUBE_ALL), TextureMap.all(allTexture));
  }

  /**
   * The block is a simple "cube_bottom_top", but is not a column that has an "axis" property.
   *
   * @param settings      The block settings.
   * @param topTexture    Texture for top side.
   * @param sideTexture   Texture for horizontal sides.
   * @param bottomTexture Texture for bottom side.
   * @return A new instance.
   */
  public static BRRPCubeBlock cubeBottomTop(Settings settings, Identifier topTexture, Identifier sideTexture, Identifier bottomTexture) {
    return new BRRPCubeBlock(settings, ModelUtils.getId(Models.CUBE_BOTTOM_TOP), new TextureMap().put(TextureKey.TOP, topTexture).put(TextureKey.SIDE, sideTexture).put(TextureKey.BOTTOM, bottomTexture));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability BlockStateSupplier getBlockStates() {
    return BlockStateModelGenerator.createSingletonBlockState(asBlock(), getBlockModelId());
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability ModelJsonBuilder getBlockModel() {
    return ModelJsonBuilder.create(parent).setTextures(textures);
  }


  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull Identifier getTextureId(@NotNull TextureKey textureKey) {
    final Identifier texture = TextureRegistry.getTexture(this, textureKey);
    if (texture != null) return texture;
    for (TextureKey textureKey0 = textureKey; textureKey0 != null; textureKey0 = textureKey0.getParent()) {
      Identifier texture0 = textures.getTexture(textureKey0);
      if (texture0 == null) continue;
      return texture0;
    }
    return BlockResourceGenerator.super.getTextureId(textureKey);
  }
}
