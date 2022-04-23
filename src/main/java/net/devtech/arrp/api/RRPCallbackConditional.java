package net.devtech.arrp.api;

import net.devtech.arrp.util.IrremovableList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import java.util.List;

/**
 * This callback is similar to {@link RRPCallback}, however it detects the environment type of the resource loaded. If you want to generate or add resource packs according to the resource loading environment, you can use this.
 *
 * @author SolidBlock
 * @since 0.6.0-snapshot.4
 * @since RRPCallback
 */
public interface RRPCallbackConditional {
  /**
   * Register your type-specific resource pack at a higher priority than minecraft and mod resources.
   */
  Event<RRPCallbackConditional> BEFORE_VANILLA = EventFactory.createArrayBacked(RRPCallbackConditional.class, r -> (resourceType, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (RRPCallbackConditional callback : r) {
      callback.insertTo(resourceType, packs);
    }
  });
  /**
   * Register your type-specific resource pack at a lower priority than minecraft and mod resources.
   */
  Event<RRPCallbackConditional> AFTER_VANILLA = EventFactory.createArrayBacked(RRPCallbackConditional.class, r -> (resourceType, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (RRPCallbackConditional callback : r) {
      callback.insertTo(resourceType, packs);
    }
  });

  void insertTo(ResourceType resourceType, List<ResourcePack> builder);
}
