package pers.solid.brrp.v1.mixin;

import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryBuilder;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinRegistries.class)
public interface BuiltinRegistriesAccessor {
  @Accessor("REGISTRY_BUILDER")
  @Contract
  static RegistryBuilder getRegistryBuilder() {
    throw new UnsupportedOperationException();
  }
}
