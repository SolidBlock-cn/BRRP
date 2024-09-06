package pers.solid.brrp.v1.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SinglePreparationResourceReloader.class)
public abstract class SinglePreparationResourceReloaderMixin {
  @ModifyReturnValue(method = "reload", at = @At("RETURN"))
  private CompletableFuture<Void> prepareForImmediateResource(CompletableFuture<Void> original, @Local(argsOnly = true) ResourceReloader.Synchronizer synchronizer, @Local(argsOnly = true) ResourceManager manager, @Local(argsOnly = true, ordinal = 0) Profiler prepareProfiler, @Local(argsOnly = true, ordinal = 1) Profiler applyProfiler, @Local(argsOnly = true, ordinal = 0) Executor prepareExecutor, @Local(argsOnly = true, ordinal = 1) Executor applyExecutor) {
    if (this instanceof ImmediateResourceLoader im) {
      return original.thenCompose(unused -> CompletableFuture.supplyAsync(() -> im.prepareImmediate$brrp(manager, prepareProfiler), prepareExecutor)
          .thenCompose(synchronizer::whenPrepared)
          .thenAcceptAsync(map -> im.applyImmediate$brrp(map, manager, applyProfiler), applyExecutor));
    }
    return original;
  }
}
