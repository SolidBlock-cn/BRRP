package pers.solid.brrp.v1.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * The special version of {@code InputSupplier<InputStream>} that may be returned by {@link net.minecraft.resource.ResourcePack#open(ResourceType, Identifier)} which provides an instance resource.
 *
 * @param <T> The type of the instant resource.
 */
public interface ImmediateInputSupplier<T> extends InputSupplier<InputStream> {
  Logger LOGGER = LoggerFactory.getLogger(ImmediateInputSupplier.class);

  ImmediateResource<T> immediateResource();

  record OfEmpty<T>(ImmediateResource<T> value) implements ImmediateInputSupplier<T> {
    @Override
    public ImmediateResource<T> immediateResource() {
      return value;
    }

    @Override
    public InputStream get() throws IOException {
      return InputStream.nullInputStream();
    }
  }

  record OfCodec<T>(Codec<T> codec, ImmediateResource<T> immediateResource) implements ImmediateInputSupplier<T> {
    @Override
    public InputStream get() throws IOException {
      LOGGER.warn("Getting immediate resource converting into binary forms!");
      final JsonElement jsonElement = codec.encodeStart(JsonOps.INSTANCE, immediateResource.apply(null)).getOrThrow();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
      RuntimeResourcePack.GSON.toJson(jsonElement, writer);
      writer.close();
      return stream.toInputStream();
    }
  }
}
