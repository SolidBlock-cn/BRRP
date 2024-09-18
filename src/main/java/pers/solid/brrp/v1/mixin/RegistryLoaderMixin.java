package pers.solid.brrp.v1.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Decoder;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.RegistryResourceFunction;
import pers.solid.brrp.v1.impl.RegistryInfoGetterExtension;
import pers.solid.brrp.v1.impl.RegistryLoaderWrapperLookup;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {

  /**
   * Store the {@link DynamicRegistryManager} in the returned {@link RegistryOps.RegistryInfoGetter} to fetch when needed.
   */
  @ModifyReturnValue(method = "createInfoGetter", at = @At("RETURN"))
  private static RegistryOps.RegistryInfoGetter storeInInfoGetter(RegistryOps.RegistryInfoGetter original, @Local(argsOnly = true) DynamicRegistryManager baseRegistryManager) {
    if (original instanceof RegistryInfoGetterExtension extension) {
      extension.setBaseRegistryManager$brrp(baseRegistryManager);
    } else {
      BRRPMixins.LOGGER.warn("BRRP: The {} returned is not extended by the mixin. This may be due to mixin not loaded successfully, or the object is replaced by other mods.", RegistryOps.RegistryInfoGetter.class.getSimpleName());
    }
    return original;
  }

  @SuppressWarnings("unchecked")
  @Inject(method = "parseAndAdd", at = @At("HEAD"), cancellable = true)
  private static <E> void modifyParseAndAdd(MutableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, RegistryKey<E> key, Resource resource, RegistryEntryInfo entryInfo, CallbackInfo ci) {
    if (((ResourceAccessor) resource).getInputSupplier() instanceof ImmediateInputSupplier<?> im) {
      try {
        Object instance = im.resource();
        if (instance instanceof RegistryResourceFunction<?> rf) {
          final RegistryOps.RegistryInfoGetter registryInfoGetter = ((RegistryOpsAccessor) ops).getRegistryInfoGetter();
          if (registryInfoGetter instanceof RegistryInfoGetterExtension extension) {
            final DynamicRegistryManager base = extension.getBaseRegistryManager$brrp();
            final RegistryWrapper.WrapperLookup tagCreatingWrapper = new RegistryLoaderWrapperLookup(base);
            instance = rf.apply(tagCreatingWrapper);
          } else {
            BRRPMixins.LOGGER.warn("BRRP: Failed to read resource because {} objects are not available. This may be due to version mismatch, mixin not loaded, or mod conflict.", RegistryWrapper.WrapperLookup.class.getSimpleName());
          }
        }
        registry.add(key, (E) instance, entryInfo);
      } catch (ClassCastException e) {
        BRRPMixins.LOGGER.warn("BRRP: Cannot add immediate dynamic registry content {} into registry {}", key.getValue(), key.getRegistry(), e);
      }
      ci.cancel();
    }
  }

}
