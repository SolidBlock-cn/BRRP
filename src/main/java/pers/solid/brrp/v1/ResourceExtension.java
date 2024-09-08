package pers.solid.brrp.v1;

import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.impl.RuntimeResourcePackImpl;
import pers.solid.brrp.v1.mixin.ResourceAccessor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface ResourceExtension {
  static Map<Identifier, Object> findExtendedResources(ResourceManager resourceManager, String dataType) {
    final Map<Identifier, Object> map = new HashMap<>();
    ResourceFinder resourceFinder = ResourceFinder.json(dataType);

    for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
      Identifier identifier = entry.getKey();

      final Resource resource = entry.getValue();
      final InputSupplier<InputStream> provider = ((ResourceAccessor) resource).getInputSupplier();
      if (provider instanceof ImmediateInputSupplier<?> im) {
        RuntimeResourcePackImpl.LOGGER.debug("ImmediateInputSupplier found: {}", identifier);
        map.put(resourceFinder.toResourceId(identifier), im.resource());
      }
    }

    return map;
  }
}
