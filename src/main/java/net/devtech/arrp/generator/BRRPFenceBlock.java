package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BRRPFenceBlock extends FenceBlock implements BlockResourceGenerator {
  /**
   * The base block of the fence block.
   */
  public final @Nullable Block baseBlock;

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  public BRRPFenceBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createFenceBlockState(
        this,
        blockModelId.brrp_append("_post"),
        blockModelId.brrp_append("_side")
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel().addTexture("texture", getTextureId("texture"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    pack.addModel(blockModel.parent("block/fence_post"), blockModelId.brrp_append("_post"));
    pack.addModel(blockModel.parent("block/fence_side"), blockModelId.brrp_append("_side"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @Nullable JModel getItemModel() {
    return getBlockModel().parent("block/fence_inventory");
  }
}
