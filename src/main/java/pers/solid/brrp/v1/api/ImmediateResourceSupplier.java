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
 * <p>The supplier that can provide direct resources instead of just providing {@code byte[]}-format data. It directly extends {@code Supplier<byte[]>} to be directly used like other indirect resources, but the byte format data will not be used, so the byte output may be empty.
 * <p>There are several implementations:</p>
 * <ul>
 *   <li>{@link OfSimpleResource}</li>
 *   <li>{@link OfRegistryResource}</li>
 *   <li>{@link OfJson}</li>
 * </ul>
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

  /**
   * The immediate resource that represents a simple resource, which does not a registry wrapper lookup to get content. The codec is used to specify the JSON output, including then dumping resource packs.
   *
   * @param <T> {@inheritDoc}
   */
  interface OfSimpleResource<T> extends ImmediateResourceSupplier<T> {
    /**
     * @return The codec used to serialize the resource.
     */
    Codec<T> codec();

    @Override
    default ImmediateInputSupplier<T> getImmediateInputSupplier() {
      return new ImmediateInputSupplier.OfSimpleResource<>(codec(), resource());
    }

    /**
     * Returns a json representation of the immediate resource. This is used when dumping resource packs and may not require a {@link RegistryWrapper.WrapperLookup} object.
     */
    default JsonElement getJsonElement(DynamicOps<JsonElement> ops) {
      return codec().encodeStart(ops, resource()).getOrThrow();
    }

    /**
     * The default simple implementation of {@link OfSimpleResource}.
     *
     * @param codec    The codec used to serialize the resource.
     * @param resource The resource.
     * @param <T>      {@inheritDoc}
     */
    record Impl<T>(Codec<T> codec, T resource) implements OfSimpleResource<T> {}
  }

  /**
   * The immediate resource that represents a resource that require a {@link RegistryWrapper.WrapperLookup} to get resources. The codec is used to specify the JSON output.
   *
   * @param <T> {@inheritDoc}
   */
  interface OfRegistryResource<T> extends ImmediateResourceSupplier<RegistryResourceFunction<T>> {
    /**
     * @return The codec used to serialize the resource.
     */
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

  /**
   * The immediate resource that represents a simple JSON. In this case, codecs are not required.
   */
  interface OfJson extends ImmediateResourceSupplier<JsonElement> {
    /**
     * @return The JSON of the resource.
     */
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
      return new ImmediateInputSupplier.OfJson(jsonElement());
    }

    /**
     * The default implementation of {@link OfJson}.
     *
     * @param jsonElement The JSON of the resource.
     */
    record Impl(JsonElement jsonElement) implements OfJson {
    }
  }
}
