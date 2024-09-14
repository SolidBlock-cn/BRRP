package pers.solid.brrp.v1.impl;

import net.minecraft.registry.RegistryWrapper;

public interface RegistryInfoGetterExtension {
  RegistryWrapper.WrapperLookup getWrapperLookup$brrp();

  void setWrapperLookup$brrp(RegistryWrapper.WrapperLookup wrapperLookup$brrp);
}
