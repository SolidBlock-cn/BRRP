package pers.solid.brrp.v1.impl;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import pers.solid.brrp.v1.api.ImmediateResource;

import java.util.Map;

public interface ImmediateResourceLoader {
  default Map<Identifier, ImmediateResource<?>> prepareImmediate$brrp(ResourceManager resourceManager, Profiler profiler) {
    throw new UnsupportedOperationException("not implemented");
  }

  default void applyImmediate$brrp(Map<Identifier, ImmediateResource<?>> prepared, ResourceManager manager, Profiler profiler) {
  }
}
