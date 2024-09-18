package pers.solid.brrp.v1.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * The special version of {@code InputSupplier<InputStream>} that may be returned by {@link net.minecraft.resource.ResourcePack#open(ResourceType, Identifier)} which provides an instance resource.
 *
 * @param <T> The type of the instant resource.
 */
@ApiStatus.AvailableSince("1.1.0")
public interface ImmediateInputSupplier<T> extends InputSupplier<InputStream> {
  Logger LOGGER = LoggerFactory.getLogger(ImmediateInputSupplier.class);

  T resource();

  record OfEmpty<T>(T resource) implements ImmediateInputSupplier<T> {
    @Override
    public InputStream get() throws IOException {
      return InputStream.nullInputStream();
    }
  }

  record OfSimpleResource<T>(Codec<T> codec, T resource) implements ImmediateInputSupplier<T> {
    @Override
    public InputStream get() throws IOException {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      RuntimeResourcePack.GSON.toJson(codec.encodeStart(JsonOps.INSTANCE, resource), new OutputStreamWriter(stream));
      stream.close();
      return new ByteArrayInputStream(stream.toByteArray());
    }
  }

  record OfRegistryResource<T>(Codec<T> codec, RegistryResourceFunction<T> resource) implements ImmediateInputSupplier<RegistryResourceFunction<T>> {
    @Override
    public InputStream get() throws IOException {
      LOGGER.warn("BRRP: Getting resource that require {} converting into binary forms. This is not supported.", RegistryWrapper.WrapperLookup.class);
      return InputStream.nullInputStream();
    }
  }

  record OfJson(JsonElement jsonElement) implements ImmediateInputSupplier<JsonElement> {
    @Override
    public JsonElement resource() {
      return jsonElement;
    }

    @Override
    public InputStream get() throws IOException {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      RuntimeResourcePack.GSON.toJson(jsonElement(), new OutputStreamWriter(stream));
      stream.close();
      return new ByteArrayInputStream(stream.toByteArray());
    }
  }
}
