package pers.solid.brrp.v1.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.registry.RegistryWrapper;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Supplier;

/**
 * The supplier that can provide direct resources instead of just providing {@code byte[]}-format data. It directly extends {@code Supplier<byte[]>} to be directly used as other indirect resources, but the byte format data will not be used, so the byte output may be empty.
 *
 * @param <T> Type of the direct resource.
 */
public interface ImmediateResourceSupplier<T> extends Supplier<byte[]> {
  /**
   * @return The immediate resource.
   */
  ImmediateResource<T> immediateResource();

  /**
   * As this is an immediate resource without the need going to binary format, the implementation only returns an empty byte array.
   */
  @Deprecated
  @Override
  default byte[] get() {
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  default ImmediateInputSupplier<T> getImmediateInputSupplier() {
    return new ImmediateInputSupplier.OfEmpty<>(immediateResource());
  }

  /**
   * The interface based on {@link ImmediateResourceSupplier} provides any additional support of being in json element forms when dumping resource packs. Dumping the resources require a {@link RegistryWrapper.WrapperLookup}.
   *
   * @param <T>
   */
  interface JsonBytesSupplier<T> extends ImmediateResourceSupplier<T> {
    Codec<T> codec();

    /**
     * Returns a json representation of the immediate resource. This is used when dumping resource packs and often requires a {@link RegistryWrapper.WrapperLookup} object.
     */
    default JsonElement getJsonElement(DynamicOps<JsonElement> ops, RegistryWrapper.WrapperLookup registryLookup) {
      return codec().encodeStart(ops, immediateResource().apply(registryLookup)).getOrThrow();
    }

    @Override
    default ImmediateInputSupplier<T> getImmediateInputSupplier() {
      return new ImmediateInputSupplier.OfCodec<>(codec(), immediateResource());
    }
  }

  /**
   * The default simple implementation of {@link ImmediateResourceSupplier}.
   */
  record JsonBytesSupplierImpl<T>(Codec<T> codec, ImmediateResource<T> immediateResource) implements JsonBytesSupplier<T> {}
}
