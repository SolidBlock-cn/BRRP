package pers.solid.brrp.v1.impl;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Visible version of {@link net.minecraft.registry.ReloadableRegistries.ReloadableWrapperLookup}.
 */

public class ReloadableWrapperLookup implements RegistryWrapper.WrapperLookup {
  private final DynamicRegistryManager registryManager;

  public ReloadableWrapperLookup(DynamicRegistryManager registryManager) {
    this.registryManager = registryManager;
  }

  @Override
  public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
    return this.registryManager.streamAllRegistryKeys();
  }

  @Override
  public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
    return this.registryManager.getOptional(registryRef).map(Registry::getTagCreatingWrapper);
  }
}