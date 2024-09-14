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

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
  /**
   * 将 {@link DynamicRegistryManager} 存储在返回的 {@link RegistryOps.RegistryInfoGetter} 中，从而在需要时读取。
   */
  @ModifyReturnValue(method = "createInfoGetter", at = @At("RETURN"))
  private static RegistryOps.RegistryInfoGetter storeInInfoGetter(RegistryOps.RegistryInfoGetter original, @Local(argsOnly = true) DynamicRegistryManager baseRegistryManager) {
    if (original instanceof RegistryInfoGetterExtension extension) {
      extension.setWrapperLookup$brrp(baseRegistryManager);
    } else {
      BRRPMixins.LOGGER.warn("The {} returned is not extended by the mixin. This may be due to mixin not loaded successfully, or the object is replaced by other mods.", RegistryOps.RegistryInfoGetter.class.getSimpleName());
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
          final RegistryOps.RegistryInfoGetter registryInfoGetter = ((RegistryOpsAccessor) ops).getRegistryInfoGetter(); // todo 检查其他的资源是否需要 tag creating
          if (registryInfoGetter instanceof RegistryInfoGetterExtension extension) {
            final RegistryWrapper.WrapperLookup wrapperLookup$brrp = extension.getWrapperLookup$brrp();
            final RegistryWrapper.WrapperLookup tagCreatingWrapper = new RegistryWrapper.WrapperLookup() {
              @Override
              public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
                return wrapperLookup$brrp.streamAllRegistryKeys();
              }

              @Override
              public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return ((DynamicRegistryManager) wrapperLookup$brrp).getOptional(registryRef).map(Registry::getTagCreatingWrapper);
              }
            };
            instance = rf.apply(tagCreatingWrapper);
          } else {
            BRRPMixins.LOGGER.warn("BRRP: Failed to read resource because {} objects are not available. This may be due to version mismatch, mixin not loaded, or mod conflict.", RegistryWrapper.WrapperLookup.class.getSimpleName());
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
