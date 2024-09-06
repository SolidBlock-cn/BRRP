package pers.solid.brrp.v1.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

import java.util.Map;
import java.util.function.Function;

@Mixin(ServerAdvancementLoader.class)
public abstract class ServerAdvancementLoaderMixin implements ImmediateResourceLoader {
  @Shadow
  @Final
  private RegistryWrapper.WrapperLookup registryLookup;

  @Shadow
  @Final
  private static Logger LOGGER;

  @Shadow
  protected abstract void validate(Identifier id, Advancement advancement);

  @Shadow
  private Map<Identifier, AdvancementEntry> advancements;

  @Shadow
  private AdvancementManager manager;

  @Override
  public void applyImmediate$brrp(Map<Identifier, Function<RegistryWrapper.WrapperLookup, ?>> map, ResourceManager manager, Profiler profiler) {
    if (map.isEmpty()) {
      return;
    }
    ImmutableMap.Builder<Identifier, AdvancementEntry> builder = ImmutableMap.builder();
    builder.putAll(this.advancements);
    map.forEach((id, function) -> {
      try {
        final Object apply = function.apply(registryLookup);
        if (!(apply instanceof Advancement advancement)) {
          LOGGER.warn("BRRP: immediate resource with id {} is not an advancement: {}", id, apply);
          return;
        }
        this.validate(id, advancement);
        builder.put(id, new AdvancementEntry(id, advancement));
      } catch (Exception var6x) {
        LOGGER.error("BRRP: Parsing error loading custom immediate advancement {}: {}", id, var6x.getMessage());
      }
    });
    this.advancements = builder.buildOrThrow();
    this.manager.addAll(this.advancements.values());

    for (PlacedAdvancement placedAdvancement : this.manager.getRoots()) {
      if (placedAdvancement.getAdvancementEntry().value().display().isPresent()) {
        AdvancementPositioner.arrangeForTree(placedAdvancement);
      }
    }
  }
}
