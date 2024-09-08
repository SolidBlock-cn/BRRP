package pers.solid.brrp.v1.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.RegistryResourceFunction;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {

  @SuppressWarnings("unchecked")
  @Inject(method = "parseAndAdd", at = @At("HEAD"), cancellable = true)
  private static <E> void modifyParseAndAdd(MutableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, RegistryKey<E> key, Resource resource, RegistryEntryInfo entryInfo, CallbackInfo ci) {
    if (((ResourceAccessor) resource).getInputSupplier() instanceof ImmediateInputSupplier<?> im) {
      try {
        Object instance = im.resource();
        if (instance instanceof RegistryResourceFunction<?> rf) {
          if (instance instanceof RegistryResourceFunction.ByInfoGetter<?> byInfoGetter) {
            instance = byInfoGetter.applyFromInfoGetter(((RegistryOpsAccessor) ops).getRegistryInfoGetter());
          } else {
            throw new IllegalArgumentException("The object added for dynamic registry must be a direct instance or %s, instead of a simple %s instance! (key = %s)".formatted(RegistryResourceFunction.ByInfoGetter.class.getSimpleName(), RegistryResourceFunction.class.getSimpleName(), key));
          }
        }
        registry.add(key, (E) instance, entryInfo);
      } catch (ClassCastException e) {
        BRRPMixins.LOGGER.warn("Cannot add immediate dynamic registry content {} into registry {}", key.getValue(), key.getRegistry(), e);
      }
      ci.cancel();
    }
  }
}
