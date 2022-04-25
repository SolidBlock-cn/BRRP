package net.devtech.arrp.mixin;

import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockStateModelGenerator.class)
public interface BlockStateModelGeneratorAccessor {
  @Invoker("createStairsBlockState")
  static BlockStateSupplier createStairsBlockState(Block stairsBlock, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId) {
    throw new AssertionError();
  }

  @Invoker("createWallBlockState")
  static BlockStateSupplier createWallBlockState(Block wallBlock, Identifier postModelId, Identifier lowSideModelId, Identifier tallSideModelId) {
    throw new AssertionError();
  }

  @Invoker("createFenceBlockState")
  static BlockStateSupplier createFenceBlockState(Block fenceBlock, Identifier postModelId, Identifier sideModelId) {
    throw new AssertionError();
  }

  @Invoker("createFenceGateBlockState")
  static BlockStateSupplier createFenceGateBlockState(Block fenceGateBlock, Identifier openModelId, Identifier closedModelId, Identifier openWallModelId, Identifier closedWallModelId) {
    throw new AssertionError();
  }
}
