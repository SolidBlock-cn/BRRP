package pers.solid.brrp.v1.impl;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;
import java.util.function.Function;

public interface ImmediateResourceLoader {
  default Map<Identifier, Function<RegistryWrapper.WrapperLookup, ?>> prepareImmediate$brrp(ResourceManager resourceManager, Profiler profiler) {
    throw new UnsupportedOperationException("not implemented");
  }

  default void applyImmediate$brrp(Map<Identifier, Function<RegistryWrapper.WrapperLookup, ?>> prepared, ResourceManager manager, Profiler profiler) {
  }
}
