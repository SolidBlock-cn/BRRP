package pers.solid.brrp.v1.mixin;

import net.minecraft.registry.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RegistryBuilder.class)
public interface RegistryBuilderAccessor {
  @Accessor
  List<?> getRegistries();
}
