package pers.solid.brrp.v1.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collections;

@Mixin(TagGroupLoader.class)
public abstract class TagGroupLoaderMixin {

  @WrapOperation(method = "loadTags", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getReader()Ljava/io/BufferedReader;"))
  private BufferedReader wrapGetReader(Resource instance, Operation<BufferedReader> original, @Share("im") LocalRef<ImmediateInputSupplier<?>> share) {
    if (((ResourceAccessor) instance).getInputSupplier() instanceof ImmediateInputSupplier<?> immediateInputSupplier) {
      share.set(immediateInputSupplier);
      return null;
    } else {
      share.set(null);
      return original.call(instance);
    }
  }

  @WrapOperation(method = "loadTags", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonParser;parseReader(Ljava/io/Reader;)Lcom/google/gson/JsonElement;", remap = false))
  private JsonElement wrapParseReader(Reader jsonReader, Operation<JsonElement> original, @Share("im") LocalRef<ImmediateInputSupplier<?>> share) {
    if (share.get() != null) {
      return null;
    } else {
      return original.call(jsonReader);
    }
  }

  @WrapOperation(method = "loadTags", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;", remap = false))
  private DataResult<TagFile> wrapTagFileParse(Codec<TagFile> instance, Dynamic<TagFile> dynamic, Operation<DataResult<TagFile>> original, @Share("im") LocalRef<ImmediateInputSupplier<?>> share) {
    if (share.get() != null) {
      return null;
    } else {
      return original.call(instance, dynamic);
    }
  }

  @WrapOperation(method = "loadTags", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow()Ljava/lang/Object;", remap = false))
  private Object wrapGetOrThrow(DataResult<TagFile> instance, Operation<TagFile> original, @Share("im") LocalRef<ImmediateInputSupplier<?>> share, @Local(ordinal = 0) Identifier identifier) {
    if (share.get() != null) {
      final Object apply = share.get().immediateResource().apply(null);
      if (apply instanceof TagFile tagFile) {
        return tagFile;
      } else {
        RuntimeResourcePack.LOGGER.warn("Immediate resource with id {} is not a TagFile: {}", identifier, apply);
        return new TagFile(Collections.emptyList(), false);
      }
    }
    return original.call(instance);
  }
}
