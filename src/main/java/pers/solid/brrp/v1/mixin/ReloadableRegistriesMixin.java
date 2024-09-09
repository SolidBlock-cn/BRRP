package pers.solid.brrp.v1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.loot.LootDataType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.ResourceExtension;
import pers.solid.brrp.v1.api.RegistryResourceFunction;
import pers.solid.brrp.v1.impl.ReloadableWrapperLookup;

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
      final var map = ResourceExtension.findExtendedResources(resourceManager, RegistryKeys.getPath(type.registryKey()));
      map.forEach((identifier, wrapperLookupFunction) -> brrp$prepareImmediate(type, mutableRegistry, identifier, wrapperLookupFunction, wrapperLookup));
      return mutableRegistry;
    });
  }

  @Unique
  @SuppressWarnings("unchecked")
  private static <T> void brrp$prepareImmediate(LootDataType<T> type, MutableRegistry<?> mutableRegistry, Identifier identifier, Object content, ReloadableWrapperLookup wrapperLookup) {
    if (content instanceof RegistryResourceFunction<?> rf) {
      content = rf.apply(wrapperLookup);
    }
    BRRPMixins.LOGGER.debug("Adding immediate resource {} to registry {}", identifier, mutableRegistry.getKey().getValue());
    ((MutableRegistry<T>) mutableRegistry).add(RegistryKey.of(type.registryKey(), identifier), (T) content, DEFAULT_REGISTRY_ENTRY_INFO);
  }
}
