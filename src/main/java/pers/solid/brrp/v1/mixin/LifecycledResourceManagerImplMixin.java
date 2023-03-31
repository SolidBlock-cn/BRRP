package pers.solid.brrp.v1.mixin;

import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pers.solid.brrp.v1.PlatformBridge;

import java.util.ArrayList;
import java.util.List;

@Mixin(LifecycledResourceManagerImpl.class)
public abstract class LifecycledResourceManagerImplMixin {
  private static final Logger LOGGER = LoggerFactory.getLogger("LifecycledResourceManagerImplMixin");

  @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
  private static List<ResourcePack> registerARRPs(List<ResourcePack> packs, ResourceType type, List<ResourcePack> packs0) {
    List<ResourcePack> copy = new ArrayList<>(packs);
    LOGGER.info("BRRP register - before vanilla");
    PlatformBridge.getInstance().postBefore(type, copy);

    LOGGER.info("BRRP register - after vanilla");
    PlatformBridge.getInstance().postAfter(type, copy);

    return copy;
  }
}