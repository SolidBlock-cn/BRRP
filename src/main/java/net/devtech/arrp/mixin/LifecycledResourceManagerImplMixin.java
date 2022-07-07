package net.devtech.arrp.mixin;

import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPCallbackForge;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Mixin(LifecycledResourceManagerImpl.class)
public abstract class LifecycledResourceManagerImplMixin {
  private static final Logger ARRP_LOGGER = LoggerFactory.getLogger("BRRP/LifecycledResourceManagerImplMixin");

  private static ResourceType resourceType;

  @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
  private static ResourceType recordResourceType(ResourceType type) {
    resourceType = type;
    return type;
  }

  @ModifyVariable(method = "<init>",
      at = @At(value = "HEAD"),
      argsOnly = true)
  private static List<ResourcePack> registerARRPs(List<ResourcePack> packs) throws ExecutionException, InterruptedException {
    ARRP.waitForPregen();

    ARRP_LOGGER.info("BRRP register - before vanilla");
    List<ResourcePack> before = new ArrayList<>();
    RRPCallbackForge.BEFORE_VANILLA.build().stream().map(f -> f.apply(resourceType)).filter(Objects::nonNull).forEach(before::add);

    before.addAll(packs);

    ARRP_LOGGER.info("BRRP register - after vanilla");
    List<ResourcePack> after = new ArrayList<>();
    RRPCallbackForge.AFTER_VANILLA.build().stream().map(f -> f.apply(resourceType)).filter(Objects::nonNull).forEach(before::add);

    return before;
  }
}