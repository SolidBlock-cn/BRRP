package pers.solid.brrp.v1.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.RegistryResourceFunction;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

import java.util.Map;

@Mixin(ServerAdvancementLoader.class)
public abstract class ServerAdvancementLoaderMixin implements ImmediateResourceLoader {
  @Shadow
  @Final
  private RegistryWrapper.WrapperLookup registryLookup;

  @Shadow
  protected abstract void validate(Identifier id, Advancement advancement);

  @Shadow
  private Map<Identifier, AdvancementEntry> advancements;

  @Shadow
  private AdvancementManager manager;

  @Override
  public void applyImmediate$brrp(Map<Identifier, Object> map, ResourceManager manager, Profiler profiler) {
    if (map.isEmpty()) {
      return;
    }
    ImmutableMap.Builder<Identifier, AdvancementEntry> builder = ImmutableMap.builder();
    builder.putAll(this.advancements);
    map.forEach((id, resource) -> {
      try {
        Object apply = resource;
        if (resource instanceof RegistryResourceFunction<?> rf) {
          apply = rf.apply(registryLookup);
        }
        if (!(apply instanceof Advancement advancement)) {
          BRRPMixins.LOGGER.warn("BRRP: immediate resource with id {} is not an advancement: {}", id, apply);
          return;
        }
        this.validate(id, advancement);
        builder.put(id, new AdvancementEntry(id, advancement));
      } catch (Exception var6x) {
        BRRPMixins.LOGGER.error("BRRP: Parsing error loading custom immediate advancement {}: {}", id, var6x.getMessage());
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
