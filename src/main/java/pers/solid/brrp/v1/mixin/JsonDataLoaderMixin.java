package pers.solid.brrp.v1.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.ResourceExtension;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.ImmediateResource;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

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
  public Map<Identifier, ImmediateResource<?>> prepareImmediate$brrp(ResourceManager resourceManager, Profiler profiler) {
    return ResourceExtension.findExtendedResources(resourceManager, dataType);
  }
}
