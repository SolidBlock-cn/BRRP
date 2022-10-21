package net.devtech.arrp.mixin;

import com.google.common.collect.Lists;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPCallbackConditional;
import net.devtech.arrp.api.SidedRRPCallback;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Mixin(LifecycledResourceManagerImpl.class)
public abstract class LifecycledResourceManagerImplMixin {
  private static final Logger BRRP_LOGGER = LoggerFactory.getLogger("BRRP/LifecycledResourceManagerImplMixin");

  @SuppressWarnings("deprecation")
  @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
  private static List<ResourcePack> registerARRPs(List<ResourcePack> packs, ResourceType type, List<ResourcePack> packs0) throws ExecutionException, InterruptedException {
    List<ResourcePack> copy = new ArrayList<>(packs);
    ARRP.waitForPregen();
    BRRP_LOGGER.info("BRRP register - before vanilla");
    SidedRRPCallback.BEFORE_VANILLA.invoker().insert(type, Lists.reverse(copy));
    RRPCallbackConditional.BEFORE_VANILLA.invoker().insertTo(type, Lists.reverse(copy));

    BRRP_LOGGER.info("BRRP register - after vanilla");
    SidedRRPCallback.AFTER_VANILLA.invoker().insert(type, copy);
    RRPCallbackConditional.AFTER_VANILLA.invoker().insertTo(type, copy);
    return copy;
  }
}