package pers.solid.brrp.v1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pers.solid.brrp.v1.impl.RuntimeResourcePackImpl;

@Mixin(RegistryBuilder.class)
public abstract class RegistryBuilderMixin {
  @Inject(method = "createWrapperLookup(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryBuilder$Registries;checkUnreferencedKeys()V", shift = At.Shift.BEFORE), cancellable = true)
  private void injectedCreateWrapperLookup(DynamicRegistryManager registryManager, CallbackInfoReturnable<RegistryWrapper.WrapperLookup> cir, @Local RegistryWrapper.WrapperLookup wrapperLookup) {
    Object thisObject = this;
    if (thisObject instanceof RuntimeResourcePackImpl.Workaround) {
      cir.setReturnValue(wrapperLookup);
    }
  }
}
