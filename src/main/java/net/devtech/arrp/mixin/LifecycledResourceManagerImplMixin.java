package net.devtech.arrp.mixin;

import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RRPCallbackConditional;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class LifecycledResourceManagerImplMixin {
  private static final Logger ARRP_LOGGER = LogManager.getLogger("ARRP/LifecycledResourceManagerImpl");

  private static ResourceType resourceType;

  @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
  private static ResourceType recordResourceType(ResourceType type) {
    resourceType = type;
    return type;
  }

  @ModifyVariable(method = "reload",
      at = @At(value = "HEAD"),
      argsOnly = true)
  private List<ResourcePack> registerARRPs(List<ResourcePack> packs) throws ExecutionException, InterruptedException {
    ARRP.waitForPregen();

    ARRP_LOGGER.info("BRRP register - before vanilla");
    List<ResourcePack> before = new ArrayList<>();
    RRPCallback.BEFORE_VANILLA.invoker().insert(before);
    RRPCallbackConditional.BEFORE_VANILLA.invoker().insertTo(resourceType, before);

    before.addAll(packs);

    ARRP_LOGGER.info("BRRP register - after vanilla");
    List<ResourcePack> after = new ArrayList<>();
    RRPCallback.AFTER_VANILLA.invoker().insert(after);
    RRPCallbackConditional.AFTER_VANILLA.invoker().insertTo(resourceType, after);

    before.addAll(after);

    return before;
  }
}