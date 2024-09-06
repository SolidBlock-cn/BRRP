package pers.solid.brrp.v1.impl;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.InputSupplier;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public interface ImmediateInputSupplier<T> extends InputSupplier<InputStream> {
  Logger LOGGER = LoggerFactory.getLogger(ImmediateInputSupplier.class);

  Function<RegistryWrapper.WrapperLookup, T> resourceProvider();

  record OfCodec<T>(Codec<T> codec, Function<RegistryWrapper.WrapperLookup, T> resourceProvider) implements ImmediateInputSupplier<T> {
    public static <T> OfCodec<T> ofTrusted(Codec<T> codec, Function<RegistryWrapper.WrapperLookup, ?> resourceProvider) {
      return new OfCodec<>(codec, wrapperLookup -> (T) resourceProvider.apply(wrapperLookup));
    }

    @Override
    public InputStream get() throws IOException {
      LOGGER.warn("Getting immediate resource converting into binary forms!");
      final JsonElement jsonElement = codec.encodeStart(JsonOps.INSTANCE, resourceProvider.apply(null)).getOrThrow();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
      RuntimeResourcePack.GSON.toJson(jsonElement, writer);
      writer.close();
      return stream.toInputStream();
    }
  }
}
