package net.devtech.arrp.api;

import net.devtech.arrp.util.IrremovableList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;

import java.util.List;

public interface RRPCallback {
  /**
   * Register your resource pack that will be read <b>before</b> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, that will be used instead. Therefore, resource packs registered here cannot override non-runtime resources.
   */
  Event<RRPCallback> BEFORE_VANILLA = EventFactory.createArrayBacked(RRPCallback.class, r -> rs -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (RRPCallback callback : r) {
      callback.insert(packs);
    }
  });

  /**
   * Register your resource pack that will be read <b>after</b> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, they will be overridden by this runtime resource. Therefore, if you want to override Minecraft vanilla resources and other non-runtime resources, you can register here.
   */
  Event<RRPCallback> AFTER_VANILLA = EventFactory.createArrayBacked(RRPCallback.class, r -> rs -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (RRPCallback callback : r) {
      callback.insert(packs);
    }
  });

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
