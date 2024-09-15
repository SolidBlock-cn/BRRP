package pers.solid.brrp.v1.impl;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps.RegistryInfoGetter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>This class is specially used for {@link pers.solid.brrp.v1.mixin.RegistryLoaderMixin} to fix the issue that {@code registryOps} cannot be used as a {@link WrapperLookup}, while {@link WrapperLookup} used in that class does not support tag creation (while the {@link RegistryInfoGetter} of the {@code registryOps} support).
 * <p>This class may be temporary.
 */
@ApiStatus.Internal
public record RegistryLoaderWrapperLookup(DynamicRegistryManager base) implements WrapperLookup {
  @Override
  public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
    return base.streamAllRegistryKeys();
  }

  @Override
  public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
    return base.getOptional(registryRef).map(Registry::getTagCreatingWrapper);
  }

  @Override
  public <T> RegistryWrapper.Impl<T> getWrapperOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
    return base.get(registryRef).getTagCreatingWrapper();
  }
}
