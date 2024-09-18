package pers.solid.brrp.v1.mixin;

import com.google.gson.JsonObject;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;

import java.io.InputStream;
import java.util.Optional;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {
  @Inject(method = "loadMetadata", at = @At("HEAD"), cancellable = true)
  private static void loadImmediateMetadata(InputSupplier<InputStream> supplier, CallbackInfoReturnable<ResourceMetadata> cir) {
    if (supplier instanceof ImmediateInputSupplier<?> im) {
      final Object resource = im.resource();
      if (resource instanceof ResourceMetadata rm) {
        cir.setReturnValue(rm);
      } else if (resource instanceof JsonObject jsonObject) {
        cir.setReturnValue(new ResourceMetadata() {
          /**
           * referenced in {@link ResourceMetadata#create(InputStream)}
           */
          @Override
          public <T> Optional<T> decode(ResourceMetadataReader<T> reader) {
            String string = reader.getKey();
            return jsonObject.has(string) ? Optional.of(reader.fromJson(JsonHelper.getObject(jsonObject, string))) : Optional.empty();
          }
        });
      } else {
        BRRPMixins.LOGGER.warn("BRRP: Immediate resource meta data found but type correct: {} not {}", resource.toString(), ResourceMetadata.class.getSimpleName());
      }
    }
  }
}
