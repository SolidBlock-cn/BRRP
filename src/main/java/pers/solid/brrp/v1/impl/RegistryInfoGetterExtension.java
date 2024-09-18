package pers.solid.brrp.v1.impl;

import net.minecraft.registry.DynamicRegistryManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("1.1.0")
public interface RegistryInfoGetterExtension {
  DynamicRegistryManager getBaseRegistryManager$brrp();

  void setBaseRegistryManager$brrp(DynamicRegistryManager dynamicRegistryManager);
}
