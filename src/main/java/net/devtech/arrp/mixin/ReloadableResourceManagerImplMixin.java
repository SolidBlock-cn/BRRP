package net.devtech.arrp.mixin;

import com.google.common.collect.Lists;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPCallbackConditional;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
  @Shadow
  @Final
  private ResourceType type;
  private static final Logger BRRP_LOGGER = LoggerFactory.getLogger("BRRP/ReloadableResourceManagerImplMixin");

  @ModifyVariable(method = "reload",
      at = @At(value = "HEAD"),
      argsOnly = true)
  private List<ResourcePack> registerARRPs(List<ResourcePack> packs) throws ExecutionException, InterruptedException {
    ARRP.waitForPregen();

    BRRP_LOGGER.info("BRRP register - before vanilla");
    List<ResourcePack> before = new ArrayList<>();
    RRPCallback.BEFORE_VANILLA.invoker().insert(before);
    RRPCallbackConditional.BEFORE_VANILLA.invoker().insertTo(resourceType, before);

    before.addAll(packs);

    BRRP_LOGGER.info("BRRP register - after vanilla");
    List<ResourcePack> after = new ArrayList<>();
    RRPCallback.AFTER_VANILLA.invoker().insert(after);
    RRPCallbackConditional.AFTER_VANILLA.invoker().insertTo(resourceType, after);

    BRRP_LOGGER.info("BRRP register - after vanilla");
    SidedRRPCallback.AFTER_VANILLA.invoker().insert(type, copy);
    RRPCallbackConditional.AFTER_VANILLA.invoker().insertTo(type, copy);
    return copy;
  }
}