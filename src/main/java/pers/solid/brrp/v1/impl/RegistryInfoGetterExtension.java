package pers.solid.brrp.v1.impl;

import net.minecraft.registry.DynamicRegistryManager;

public interface RegistryInfoGetterExtension {
  DynamicRegistryManager getBaseRegistryManager$brrp();

  void setBaseRegistryManager$brrp(DynamicRegistryManager dynamicRegistryManager);
}
