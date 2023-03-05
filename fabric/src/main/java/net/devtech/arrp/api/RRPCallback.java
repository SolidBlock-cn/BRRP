package net.devtech.arrp.api;

import net.devtech.arrp.api.fabric.ForwardingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resource.ResourcePack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

/**
 * The Fabric callback to register packs. If you're finding a method that is available both for Fabric and for Forge, you may see {@link RRPEventHelper}.
 */
public interface RRPCallback {
  @ApiStatus.AvailableSince("0.8.2")
  Function<RRPCallback, SidedRRPCallback> FORWARDING_FUNCTION = rrpCallback -> (type, resources) -> rrpCallback.insert(resources);
  @ApiStatus.AvailableSince("0.8.2")
  @ApiStatus.Internal
  Function<SidedRRPCallback, RRPCallback> INVOKER_FUNCTION = sidedRRPCallback -> resources -> sidedRRPCallback.insert(null, resources);

  /**
   * Register your resource pack that will be read <b>before</b> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, that will be used instead. Therefore, resource packs registered here cannot override non-runtime resources.
   *
   * @see RRPEventHelper#BEFORE_VANILLA
   */
  Event<RRPCallback> BEFORE_VANILLA = new ForwardingEvent<>(FORWARDING_FUNCTION, SidedRRPCallback.BEFORE_VANILLA, INVOKER_FUNCTION);

  /**
   * Register your resource pack that will be read <b>after</b> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, they will be overridden by this runtime resource. Therefore, if you want to override Minecraft vanilla resources and other non-runtime resources, you can register here.
   *
   * @see RRPEventHelper#BEFORE_VANILLA
   */
  Event<RRPCallback> AFTER_VANILLA = new ForwardingEvent<>(FORWARDING_FUNCTION, SidedRRPCallback.AFTER_VANILLA, INVOKER_FUNCTION);

  /**
   * @see #BEFORE_VANILLA
   * @deprecated unintuitive name
   */
  @Deprecated
  Event<RRPCallback> EVENT = BEFORE_VANILLA;

  /**
   * In this method, you should add your resource pack into your resources. You can also <i>generate</i> resources in this method, but it's not preferred to do this unless in development environment.
   *
   * @param resources You should add your runtime resource pack here. The list is irremovable.
   */
  void insert(List<ResourcePack> resources);
}
