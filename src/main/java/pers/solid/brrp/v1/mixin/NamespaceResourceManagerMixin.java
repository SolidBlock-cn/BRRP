package pers.solid.brrp.v1.mixin;

import net.minecraft.resource.NamespaceResourceManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {/*
  @ModifyExpressionValue(method = "getResource", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/NamespaceResourceManager;createResource(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/InputSupplier;Lnet/minecraft/resource/InputSupplier;)Lnet/minecraft/resource/Resource;"))
  private Resource setImmediateResourceInGetResource(Resource original, @Local(ordinal = 0) InputSupplier<InputStream> inputSupplier) {
    if (inputSupplier instanceof ImmediateInputSupplier<?> im) {
      ((ResourceExtension) original).setResourceProvider$brrp(im.resourceProvider());
    }
    return original;
  }

  @ModifyExpressionValue(method = "getAllResources", at = @At(value = "NEW", target = "(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/resource/InputSupplier;Lnet/minecraft/resource/InputSupplier;)Lnet/minecraft/resource/Resource;"))
  private Resource setImmediateResourceInGetAllResources(Resource original, @Local(ordinal = 0) InputSupplier<InputStream> inputSupplier) {
    if (inputSupplier instanceof ImmediateInputSupplier<?> im) {
      ((ResourceExtension) original).setResourceProvider$brrp(im.resourceProvider());
    }
    return original;
  }*/
}
