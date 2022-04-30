package net.devtech.arrp.api;

import net.devtech.arrp.util.IrremovableList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;

import java.util.List;

public interface RRPCallback {
  /**
   * Register your resource pack at a higher priority than minecraft and mod resources
   */
  Event<RRPCallback> BEFORE_VANILLA = EventFactory.createArrayBacked(RRPCallback.class, r -> rs -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (RRPCallback callback : r) {
      callback.insert(packs);
    }
  });

  /**
   * Register your resource pack at a lower priority than minecraft and mod resources
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
   * In this method, you should add your resource pack into your resources. You can also <i>generate</i> resources in this method, but it's not preferred to do this unless in development environment. The list is irremovable.
   */
  void insert(List<ResourcePack> resources);
}
