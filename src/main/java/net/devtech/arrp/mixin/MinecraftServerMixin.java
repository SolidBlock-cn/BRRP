package net.devtech.arrp.mixin;

import net.devtech.arrp.api.RRPCallback;
import net.minecraft.resource.ResourcePack;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
  private static final Logger ARRP_LOGGER = LoggerFactory.getLogger(MinecraftServerMixin.class);

  @ModifyArg(method = "reloadResources", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCompose(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;"))
  private <U> Function<List<ResourcePack>, ? extends CompletionStage<U>> registerARRPs(Function<List<ResourcePack>, ? extends CompletionStage<U>> fn) {
    return t -> {
      ARRP_LOGGER.info("BRRP register - before vanilla");
      List<ResourcePack> before = new ArrayList<>();
      RRPCallback.BEFORE_VANILLA.invoker().insert(before);

      before.addAll(t);

      ARRP_LOGGER.info("BRRP register - after vanilla");
      List<ResourcePack> after = new ArrayList<>();
      RRPCallback.AFTER_VANILLA.invoker().insert(after);

      before.addAll(after);
      return fn.apply(before);
    };
  }
}
