package net.devtech.arrp.api;

import net.devtech.arrp.util.IrremovableList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Similar to {@link RRPCallback} but can specify resource types.
 *
 * @author Deman Kervan
 * @since 0.6.4
 */
@ApiStatus.AvailableSince("0.6.4")
public interface SidedRRPCallback {
  /**
   * Register your resource pack that will be read <b>before</b> Minecraft and regular resources are loaded in the specific resource type. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, that will be used instead. Therefore, resource packs registered here cannot override non-runtime resources.
   */
  Event<SidedRRPCallback> BEFORE_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, r -> (type, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (SidedRRPCallback callback : r) {
      callback.insert(type, packs);
    }
  });

  /**
   * Register your resource pack that will be read <b>after</b> Minecraft and regular resources are loaded in the specific resource type. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, they will be overridden by this runtime resource. Therefore, if you want to override Minecraft vanilla resources and other non-runtime resources, you can register here.
   */
  Event<SidedRRPCallback> AFTER_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, r -> (type, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (SidedRRPCallback callback : r) {
      callback.insert(type, packs);
    }
  });


  /**
   * In this method, you can check the resource type and add your resource pack into your resources if the resource type meets your condition. You can also <i>generate</i> resources in this method, but it's not preferred to do this unless in development environment.
   *
   * @param type      The type of resource that will be loaded.
   * @param resources You should add your runtime resource pack here. The list is irremovable.
   */
  void insert(ResourceType type, List<ResourcePack> resources);
}