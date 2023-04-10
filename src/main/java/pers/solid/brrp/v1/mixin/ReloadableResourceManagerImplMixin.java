package pers.solid.brrp.v1.mixin;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pers.solid.brrp.v1.PlatformBridge;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
  @Shadow
  @Final
  private ResourceType type;
  private static final Logger LOGGER = LogManager.getLogger("BRRP/ReloadableResourceManagerImplMixin");

  @ModifyVariable(method = "reload", at = @At("HEAD"), argsOnly = true)
  private List<ResourcePack> register(List<ResourcePack> packs) {
    List<ResourcePack> copy = new ArrayList<>(packs);
    LOGGER.info("BRRP register - before vanilla");
    PlatformBridge.getInstance().postBefore(type, copy);

    LOGGER.info("BRRP register - after vanilla");
    PlatformBridge.getInstance().postAfter(type, copy);

    return copy;
  }
}