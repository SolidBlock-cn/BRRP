package net.devtech.arrp.generator;

import net.devtech.arrp.IdentifierExtension;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.BlockStatesDefinition;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a simple extension of {@link SlabBlock} with the resource generation provided.
 */
public class SmartSlabBlock extends SlabBlock implements BlockResourceGenerator {
  /**
   * The base block will be used to generate some files. It can be null.<br>
   * When the base block is null, the double-slab creates and uses the model or "slab_double" model, instead of directly using the model of base block.
   */
  public final @Nullable Block baseBlock;

  /**
   * Simply creates an instance with a given base block. The block settings of the base block will be used, so you do not need to provide it.
   */
  public SmartSlabBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  public SmartSlabBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  /**
   * Directly creates an instance without giving the base block.
   */
  public SmartSlabBlock(Settings settings) {
    this(null, settings);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @Nullable BlockStatesDefinition getBlockStatesDefinition() {
    final Identifier id = getBlockModelId();
    return BlockStatesDefinition.simpleSlab(baseBlock != null ? ((BlockResourceGenerator) baseBlock).getBlockModelId() : ((IdentifierExtension) id).append("_double"), id, ((IdentifierExtension) id).append("_top"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @Nullable JModel getBlockModel() {
    return new JModel("block/slab").textures(JTextures.ofSides(
        getTextureId("top"),
        getTextureId("side"),
        getTextureId("bottom")
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull String getTextureId(@Nullable String type) {
    final String texture = TextureRegistry.getTexture(this, type);
    if (texture != null) return texture;
    if (baseBlock != null) {
      return ((BlockResourceGenerator) baseBlock).getTextureId(type);
    } else {
      return BlockResourceGenerator.super.getTextureId(type);
    }
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel model = getBlockModel();
    if (model != null) {
      final Identifier id = getBlockModelId();
      pack.addModel(model, id);
      pack.addModel(model.clone().parent("block/slab_top"), ((IdentifierExtension) id).append("_top"));
      if (baseBlock == null) {
        pack.addModel(model.clone().parent("block/cube_bottom_top"), ((IdentifierExtension) id).append("_double"));
      }
    }
  }
}
