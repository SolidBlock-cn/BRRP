package pers.solid.brrp.v1.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {

  @WrapOperation(method = {"method_45898", "method_45890"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getReader()Ljava/io/BufferedReader;"))
  private static BufferedReader modifyResourceReading(Resource instance, Operation<BufferedReader> original, @Share("ir") LocalRef<JsonElement> share, @Local(argsOnly = true) Map.Entry<Identifier, Resource> entry) {
    if (((ResourceAccessor) instance).getInputSupplier() instanceof ImmediateInputSupplier.OfJson im) {
      BRRPMixins.LOGGER.debug("Loading immediate model json for {}", entry.getKey());
      share.set(im.jsonElement());
    } else {
      share.set(null);
    }
    return original.call(instance);
  }

  @WrapOperation(method = "method_45898", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;deserialize(Ljava/io/Reader;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"))
  private static JsonUnbakedModel modifyDeserializeModel(Reader input, Operation<JsonUnbakedModel> original, @Share("ir") LocalRef<JsonElement> share) {
    if (share.get() != null) {
      return JsonUnbakedModelAccessor.getGSON().fromJson(share.get(), JsonUnbakedModel.class);
    } else {
      return original.call(input);
    }
  }

  @WrapOperation(method = "method_45890", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;deserialize(Ljava/io/Reader;)Lcom/google/gson/JsonObject;"))
  private static JsonObject modifyDeserializeBlockStates(Reader reader, Operation<JsonObject> original, @Share("ir") LocalRef<JsonElement> share) {
    if (share.get() != null) {
      return (JsonObject) share.get();
    } else {
      return original.call(reader);
    }
  }
}
