package pers.solid.brrp.v1.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(JsonDataLoader.class)
public abstract class JsonDataLoaderMixin implements ImmediateResourceLoader {
  @Shadow
  @Final
  private String dataType;

  @ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
  private static Iterator<Map.Entry<Identifier, Resource>> skipResourcesWithImmediateValue(Iterator<Map.Entry<Identifier, Resource>> original) {
    return Iterators.filter(original, entry -> !(((ResourceAccessor) entry.getValue()).getInputSupplier() instanceof ImmediateInputSupplier<?>));
  }

  @Override
  public Map<Identifier, Object> prepareImmediate$brrp(ResourceManager resourceManager, Profiler profiler) {
    final Map<Identifier, Object> map = new HashMap<>();
    ResourceFinder resourceFinder = ResourceFinder.json(dataType);

    for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
      Identifier identifier = entry.getKey();
      final Resource resource = entry.getValue();
      final InputSupplier<InputStream> provider = ((ResourceAccessor) resource).getInputSupplier();
      if (provider instanceof ImmediateInputSupplier<?> im) {
        BRRPMixins.LOGGER.debug("BRRP: ImmediateInputSupplier found: {}", identifier);
        map.put(resourceFinder.toResourceId(identifier), im.resource());
      }
    }

    if (!map.isEmpty()) {
      BRRPMixins.LOGGER.info("BRRP: Loaded {} immediate resources for data type: {}", map.size(), dataType);
    }

    return map;
  }
}
