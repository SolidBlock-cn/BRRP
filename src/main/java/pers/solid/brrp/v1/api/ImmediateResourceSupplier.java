package pers.solid.brrp.v1.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import org.apache.commons.lang3.ArrayUtils;
import pers.solid.brrp.v1.mixin.RegistryOpsAccessor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
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
  T resource();

  /**
   * As this is an immediate resource without the need going to binary format, the implementation only returns an empty byte array.
   */
  @Override
  default byte[] get() {
    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  default ImmediateInputSupplier<T> getImmediateInputSupplier() {
    return new ImmediateInputSupplier.OfEmpty<>(resource());
  }

  interface OfSimpleResource<T> extends ImmediateResourceSupplier<T> {
    Codec<T> codec();

    @Override
    default ImmediateInputSupplier<T> getImmediateInputSupplier() {
      return ImmediateResourceSupplier.super.getImmediateInputSupplier();
    }

    /**
     * Returns a json representation of the immediate resource. This is used when dumping resource packs and may not require a {@link RegistryWrapper.WrapperLookup} object.
     */
    default JsonElement getJsonElement(DynamicOps<JsonElement> ops) {
      return codec().encodeStart(ops, resource()).getOrThrow();
    }

    record Impl<T>(Codec<T> codec, T resource) implements OfSimpleResource<T> {}
  }

  /**
   * The interface based on {@link ImmediateResourceSupplier} provides any additional support of being in json element forms when dumping resource packs. Dumping the resources require a {@link RegistryWrapper.WrapperLookup}.
   *
   * @param <T>
   */
  interface OfRegistryResource<T> extends ImmediateResourceSupplier<RegistryResourceFunction<T>> {
    Codec<T> codec();

    /**
     * Returns a json representation of the immediate resource. This is used when dumping resource packs and often requires a {@link RegistryWrapper.WrapperLookup} object.
     */
    default JsonElement getJsonElement(RegistryOps<JsonElement> ops, RegistryWrapper.WrapperLookup registryLookup) {
      final T apply;
      if (resource() instanceof RegistryResourceFunction.ByInfoGetter<T> byInfoGetter) {
        apply = byInfoGetter.applyFromInfoGetter(((RegistryOpsAccessor) ops).getRegistryInfoGetter());
      } else {
        apply = resource().apply(registryLookup);
      }
      return codec().encodeStart(ops, apply).getOrThrow();
    }

    @Override
    default ImmediateInputSupplier<RegistryResourceFunction<T>> getImmediateInputSupplier() {
      return new ImmediateInputSupplier.OfRegistryResource<>(codec(), resource());
    }

    /**
     * The default simple implementation of {@link ImmediateResourceSupplier}.
     */
    record Impl<T>(Codec<T> codec, RegistryResourceFunction<T> resource) implements OfRegistryResource<T> {}
  }

  interface OfJson extends ImmediateResourceSupplier<JsonElement> {
    JsonElement jsonElement();

    @Override
    default byte[] get() {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      RuntimeResourcePack.GSON.toJson(jsonElement(), new OutputStreamWriter(stream));
      return stream.toByteArray();
    }

    @Override
    default JsonElement resource() {
      return jsonElement();
    }

    @Override
    default ImmediateInputSupplier<JsonElement> getImmediateInputSupplier() {
      return new ImmediateInputSupplier.OfJsonElement(jsonElement());
    }

    record Impl(JsonElement jsonElement) implements OfJson {
    }
  }
}
