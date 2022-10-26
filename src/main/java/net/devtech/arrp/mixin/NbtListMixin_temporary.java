package net.devtech.arrp.mixin;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.AbstractList;

@Mixin(NbtList.class)
public abstract class NbtListMixin_temporary extends AbstractList<NbtElement> {
  @Shadow
  public abstract void method_10531(int i, NbtElement nbtElement);

  @Shadow
  public abstract NbtElement method_10606(int i, NbtElement nbtElement);

  @Override
  public void add(int index, NbtElement element) {
    method_10531(index, element);
  }

  @Override
  public NbtElement set(int index, NbtElement element) {
    return method_10606(index, element);
  }
}
