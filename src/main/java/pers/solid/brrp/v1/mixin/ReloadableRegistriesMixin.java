package pers.solid.brrp.v1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.loot.LootDataType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.RegistryResourceFunction;
import pers.solid.brrp.v1.impl.ReloadableWrapperLookup;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ReloadableRegistries.class)
public abstract class ReloadableRegistriesMixin {

  @Shadow
  @Final
  private static RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO;

  @ModifyArg(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
  private static Function<? super LootDataType<?>, ? extends CompletableFuture<MutableRegistry<?>>> loadImmediateResources(Function<? super LootDataType<?>, ? extends CompletableFuture<MutableRegistry<?>>> mapper, @Local DynamicRegistryManager.Immutable immutable, @Local(argsOnly = true) ResourceManager resourceManager) {
    final ReloadableWrapperLookup wrapperLookup = new ReloadableWrapperLookup(immutable);
    return type -> mapper.apply(type).thenApplyAsync(mutableRegistry -> {
      String dataType = RegistryKeys.getPath(type.registryKey());
      ResourceFinder resourceFinder = ResourceFinder.json(dataType);

      for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
        Identifier identifier = entry.getKey();

        final Resource resource = entry.getValue();
        final InputSupplier<InputStream> provider = ((ResourceAccessor) resource).getInputSupplier();
        if (provider instanceof ImmediateInputSupplier<?> im) {
          BRRPMixins.LOGGER.debug("BRRP: ImmediateInputSupplier found: {}", identifier);
          brrp$prepareImmediate(type, mutableRegistry, resourceFinder.toResourceId(identifier), im.resource(), wrapperLookup);
        }
      }

      return mutableRegistry;
    });
  }

  @Unique
  @SuppressWarnings("unchecked")
  private static <T> void brrp$prepareImmediate(LootDataType<T> type, MutableRegistry<?> mutableRegistry, Identifier identifier, Object content, ReloadableWrapperLookup wrapperLookup) {
    if (content instanceof RegistryResourceFunction<?> rf) {
      content = rf.apply(wrapperLookup);
    }
    BRRPMixins.LOGGER.debug("BRRP: Adding immediate resource {} to registry {}", identifier, mutableRegistry.getKey().getValue());
    ((MutableRegistry<T>) mutableRegistry).add(RegistryKey.of(type.registryKey(), identifier), (T) content, DEFAULT_REGISTRY_ENTRY_INFO);
  }
}
