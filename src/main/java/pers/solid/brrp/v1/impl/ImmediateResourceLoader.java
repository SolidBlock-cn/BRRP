package pers.solid.brrp.v1.impl;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public interface ImmediateResourceLoader {
  default Map<Identifier, Object> prepareImmediate$brrp(ResourceManager resourceManager, Profiler profiler) {
    throw new UnsupportedOperationException("not implemented");
  }

  default void applyImmediate$brrp(Map<Identifier, Object> prepared, ResourceManager manager, Profiler profiler) {
  }
}
