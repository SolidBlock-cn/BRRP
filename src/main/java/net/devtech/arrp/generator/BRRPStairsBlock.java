package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.BlockStatesDefinition;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BRRPStairsBlock extends StairsBlock implements BlockResourceGenerator {
  public final Block baseBlock;

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull BlockStatesDefinition getBlockStatesDefinition() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStatesDefinition.delegate(BlockStateModelGenerator.createStairsBlockState(this, blockModelId.brrp_append("_inner"), blockModelId, blockModelId.brrp_append("_outer")));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel("block/stairs").textures(JTextures.ofSides(getTextureId("top"), getTextureId("side"), getTextureId("bottom")));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier id = getBlockModelId();
    pack.addModel(blockModel, id);
    pack.addModel(blockModel.parent("block/inner_stairs"), id.brrp_append("_inner"));
    pack.addModel(blockModel.parent("block/outer_stairs"), id.brrp_append("_outer"));
  }

  private BRRPStairsBlock(BlockState baseBlockState, Settings settings) {
    super(baseBlockState, settings);
    this.baseBlock = baseBlockState.getBlock();
  }

  public BRRPStairsBlock(Block baseBlock, Settings settings) {
    super(baseBlock.getDefaultState(), settings);
    this.baseBlock = baseBlock;
  }

  public BRRPStairsBlock(Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull String getTextureId(@Nullable String type) {
    final String texture = TextureRegistry.getTexture(this, type);
    if (texture != null) return texture;
    if (baseBlock != null) {
      return ResourceGeneratorHelper.getTextureId(baseBlock, type);
    } else {
      return BlockResourceGenerator.super.getTextureId(type);
    }
  }
}
