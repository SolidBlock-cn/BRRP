package net.devtech.arrp.mixin;

import net.devtech.arrp.ARRP;
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
import pers.solid.brrp.PlatformBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
  @Shadow
  @Final
  private ResourceType type;
  private static final Logger LOGGER = LogManager.getLogger("BRRP/ReloadableResourceManagerImplMixin");

  @ModifyVariable(method = "reload", at = @At("HEAD"), argsOnly = true)
  private List<ResourcePack> registerARRPs(List<ResourcePack> packs) throws ExecutionException, InterruptedException {
    List<ResourcePack> copy = new ArrayList<>(packs);
    ARRP.waitForPregen();

    LOGGER.info("BRRP register - before vanilla");
    PlatformBridge.getInstance().postBefore(type, copy);

    LOGGER.info("BRRP register - after vanilla");
    PlatformBridge.getInstance().postAfter(type, copy);

    return copy;
  }
}