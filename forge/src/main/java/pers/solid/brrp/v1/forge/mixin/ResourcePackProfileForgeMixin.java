package pers.solid.brrp.v1.forge.mixin;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.function.Supplier;

@Mixin(ResourcePackProfile.class)
public class ResourcePackProfileForgeMixin {
  @Mutable
  @Shadow
  @Final
  private ResourcePackCompatibility compatibility;

  @Inject(at = @At("TAIL"), method = "<init>(Ljava/lang/String;ZLjava/util/function/Supplier;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;Lnet/minecraft/resource/ResourcePackCompatibility;Lnet/minecraft/resource/ResourcePackProfile$InsertionPosition;ZLnet/minecraft/resource/ResourcePackSource;Z)V")
  private void forceCompatible(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, ResourcePackProfile.InsertionPosition direction, boolean pinned, ResourcePackSource source, boolean hidden, CallbackInfo ci) {
    if (source == RuntimeResourcePack.RUNTIME) {
      this.compatibility = ResourcePackCompatibility.COMPATIBLE;
    }
  }
}
