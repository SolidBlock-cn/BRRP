package net.devtech.arrp.mixin;

import net.devtech.arrp.generator.BlockResourceGenerator;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockResourceGenerator {
}
