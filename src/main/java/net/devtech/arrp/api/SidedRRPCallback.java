package net.devtech.arrp.api;

import net.devtech.arrp.util.IrremovableList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * @author Deman Kervan
 * @since 0.6.4
 */
@ApiStatus.AvailableSince("0.6.4")
public interface SidedRRPCallback {
  /**
   * Register your resource pack at a higher priority than minecraft and mod resources, this is actually done by passing a reversed view of the
   * resource pack list, such that List#add will actually add to the beginning of the list instead of the end.
   * <p>
   * If you want to override a vanilla resource
   */
  Event<SidedRRPCallback> BEFORE_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, r -> (type, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (SidedRRPCallback callback : r) {
      callback.insert(type, packs);
    }
  });

  /**
   * Register your resource pack at a lower priority than minecraft and mod resources. This is actually done by passing a view of the resource pack
   * list, such that List#add will add to the end of the list, after default resource packs.
   */
  Event<SidedRRPCallback> AFTER_VANILLA = EventFactory.createArrayBacked(SidedRRPCallback.class, r -> (type, rs) -> {
    IrremovableList<ResourcePack> packs = new IrremovableList<>(rs, $ -> {
    });
    for (SidedRRPCallback callback : r) {
      callback.insert(type, packs);
    }
  });


  /**
   * @see net.minecraft.resource.LifecycledResourceManagerImpl#LifecycledResourceManagerImpl(ResourceType, List)
   */
  void insert(ResourceType type, List<ResourcePack> resources);
}