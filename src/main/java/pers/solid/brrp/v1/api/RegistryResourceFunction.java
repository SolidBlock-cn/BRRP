package pers.solid.brrp.v1.api;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.mixin.RegistryOpsAccessor;

import java.util.function.Function;

@FunctionalInterface
public interface RegistryResourceFunction<T> extends Function<RegistryWrapper.WrapperLookup, T> {
  @Override
  T apply(RegistryWrapper.WrapperLookup registryLookup);

  default ImmediateResourceSupplier.OfRegistryResource<T> toBytesSupplier(Codec<T> codec) {
    return new ImmediateResourceSupplier.OfRegistryResource.Impl<>(codec, this);
  }

  interface ByInfoGetter<T> extends RegistryResourceFunction<T> {
    /**
     * In occasions where you have a direct {@link RegistryOps.RegistryInfoGetter} object, you may call {@link #applyFromInfoGetter(RegistryOps.RegistryInfoGetter)} instead.
     */
    @Deprecated
    @Override
    default T apply(RegistryWrapper.WrapperLookup registryLookup) {
      BRRPMixins.LOGGER.warn("Calling 'apply' with ByInfoGetter!");
      return applyFromInfoGetter(((RegistryOpsAccessor) registryLookup.getOps(NbtOps.INSTANCE)).getRegistryInfoGetter());
    }

    T applyFromInfoGetter(RegistryOps.RegistryInfoGetter registryInfoGetter);
  }
}
