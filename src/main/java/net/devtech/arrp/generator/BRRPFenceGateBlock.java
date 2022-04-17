package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BRRPFenceGateBlock extends FenceGateBlock implements BlockResourceGenerator {
  public final @Nullable Block baseBlock;

  public BRRPFenceGateBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceGateBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JBlockStates getBlockStatesDefinition() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createFenceGateBlockState(
        this,
        blockModelId.brrp_append("_open"),
        blockModelId,
        blockModelId.brrp_append("_wall_open"),
        blockModelId.brrp_append("_wall")
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel("block/template_fence_gate").addTexture("texture", getTextureId("texture"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    pack.addModel(blockModel, blockModelId);
    pack.addModel(blockModel.parent("block/template_fence_gate_open"), blockModelId.brrp_append("_open"));
    pack.addModel(blockModel.parent("block/template_fence_gate_wall"), blockModelId.brrp_append("_wall"));
    pack.addModel(blockModel.parent("block/template_fence_gate_wall_open"), blockModelId.brrp_append("_wall_open"));
  }
}
