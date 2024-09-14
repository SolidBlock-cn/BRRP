package pers.solid.brrp.v1.mixin;

import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pers.solid.brrp.v1.impl.RegistryInfoGetterExtension;

@Mixin(targets = "net/minecraft/registry/RegistryLoader$1")
public abstract class RegistryInfoGetterAyMixin implements RegistryInfoGetterExtension {
  @Unique
  private RegistryWrapper.WrapperLookup wrapperLookup$brrp;

  @Override
  public RegistryWrapper.WrapperLookup getWrapperLookup$brrp() {
    return wrapperLookup$brrp;
  }

  @Override
  public void setWrapperLookup$brrp(RegistryWrapper.WrapperLookup wrapperLookup) {
    this.wrapperLookup$brrp = wrapperLookup;
  }
}
