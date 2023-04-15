package pers.solid.brrp.v1.fabric.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.v1.RRPEventHelper;

import java.util.List;
import java.util.function.Function;

/**
 * Similar to {@link RRPCallback} but can specify resource types. It is also supported by {@link RRPEventHelper}.
 *
 * @author Deman Kervan
 * @since 0.6.4
 */
public interface SidedRRPCallback {
  Function<SidedRRPCallback[], SidedRRPCallback> CALLBACK_FUNCTION = callbacks -> (type, resourcePacks) -> {
    for (SidedRRPCallback callback : callbacks) {
      callback.insert(type, resourcePacks);
    }
  };

  /**
   * Register your resource pack that will be read <strong>before</strong> Minecraft and regular resources are loaded in the specific resource type. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, that will be used instead. Therefore, resource packs registered here cannot override non-runtime resources.
   *
   * @see RRPEventHelper#BEFORE_VANILLA
   */
  Event<SidedRRPCallback> BEFORE_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, CALLBACK_FUNCTION);

  @ApiStatus.Experimental
  Event<SidedRRPCallback> BEFORE_USER = EventFactory.createArrayBacked(SidedRRPCallback.class, CALLBACK_FUNCTION);

  /**
   * Register your resource pack that will be read <strong>after</strong> Minecraft and regular resources are loaded in the specific resource type. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, they will be overridden by this runtime resource. Therefore, if you want to override Minecraft vanilla resources and other non-runtime resources, you can register here.
   *
   * @see RRPEventHelper#AFTER_VANILLA
   */
  Event<SidedRRPCallback> AFTER_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, CALLBACK_FUNCTION);


  /**
   * In this method, you can check the resource type and add your resource pack into your resources if the resource type meets your condition.
   *
   * @param type      The type of resource that will be loaded.
   * @param resources You should add your runtime resource pack here. The list is irremovable.
   */
  void insert(ResourceType type, List<ResourcePack> resources);
}