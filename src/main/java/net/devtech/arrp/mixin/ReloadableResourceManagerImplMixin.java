package net.devtech.arrp.mixin;

import net.devtech.arrp.api.RRPCallback;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
  private static final Logger ARRP_LOGGER = LoggerFactory.getLogger("ARRP/ReloadableResourceManagerImplMixin");

  //  @ModifyVariable(method = "reload",
//      at = @At(value = "HEAD"), argsOnly = true)
  private List<ResourcePack> registerARRPs(List<ResourcePack> packs, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs0) {
    ARRP_LOGGER.info("BRRP register - before vanilla");
    List<ResourcePack> before = new ArrayList<>();
    RRPCallback.BEFORE_VANILLA.invoker().insert(before);

    before.addAll(packs);

    ARRP_LOGGER.info("BRRP register - after vanilla");
    List<ResourcePack> after = new ArrayList<>();
    RRPCallback.AFTER_VANILLA.invoker().insert(after);

    before.addAll(after);

    return before;
  }
}