package pers.solid.brrp.v1.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;

import java.io.InputStream;

@Environment(EnvType.CLIENT)
@Mixin(SpriteOpener.class)
public interface SpriteOwnerMixin {
  @WrapOperation(method = "method_52851", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getInputStream()Ljava/io/InputStream;"))
  private static InputStream ignoreWhenImmediateNativeImage(Resource instance, Operation<InputStream> original, @Share("nativeImage") LocalRef<NativeImage> share, @Local(argsOnly = true) Identifier id) {
    if (((ResourceAccessor) instance).getInputSupplier() instanceof ImmediateInputSupplier<?> ofSimpleResource) {
      if (ofSimpleResource.resource() instanceof NativeImage ni) {
        share.set(ni);
        return null;
      } else {
        BRRPMixins.LOGGER.error("BRRP: The immediate resource with id {} is not an instance of {}, ignored.", id, NativeImage.class.getSimpleName());
      }
    }
    return original.call(instance);
  }

  @WrapOperation(method = "method_52851", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;"))
  private static NativeImage readImmediateNativeImate(InputStream stream, Operation<NativeImage> original, @Share("nativeImage") LocalRef<NativeImage> share) {
    if (share.get() != null) {
      return share.get();
    }
    return original.call(stream);
  }
}
