package pers.solid.brrp.v1.api;

import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryWrapper;

import java.util.function.Function;

@FunctionalInterface
public interface ImmediateResource<T> extends Function<RegistryWrapper.WrapperLookup, T> {
  @Override
  T apply(RegistryWrapper.WrapperLookup registryLookup);

  default ImmediateResourceSupplier.JsonBytesSupplier<T> toBytesSupplier(Codec<T> codec) {
    return new ImmediateResourceSupplier.JsonBytesSupplierImpl<>(codec, this);
  }
}
