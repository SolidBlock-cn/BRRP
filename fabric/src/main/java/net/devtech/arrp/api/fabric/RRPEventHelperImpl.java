package net.devtech.arrp.api.fabric;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RRPEventHelper;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.api.SidedRRPCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class RRPEventHelperImpl extends RRPEventHelper {
  public static final RRPEventHelperImpl BEFORE_VANILLA = new RRPEventHelperImpl(RRPCallback.BEFORE_VANILLA, SidedRRPCallback.BEFORE_VANILLA);
  public static final RRPEventHelperImpl AFTER_VANILLA = new RRPEventHelperImpl(RRPCallback.AFTER_VANILLA, SidedRRPCallback.AFTER_VANILLA);
  public final Event<RRPCallback> callback;
  public final Event<SidedRRPCallback> sidedCallback;

  public RRPEventHelperImpl(Event<RRPCallback> callback, Event<SidedRRPCallback> sidedCallback) {
    this.callback = callback;
    this.sidedCallback = sidedCallback;
  }

  public static RRPEventHelper getBeforeVanilla() {
    return BEFORE_VANILLA;
  }

  public static RRPEventHelper getAfterVanilla() {
    return AFTER_VANILLA;
  }

  public void registerPack(RuntimeResourcePack pack) {
    callback.register(resources -> resources.add(pack));
  }

  public void registerSidedPack(ResourceType resourceType, RuntimeResourcePack pack) {
    sidedCallback.register((type, resources) -> {
      if (type == resourceType) resources.add(pack);
    });
  }

  public void registerPack(@NotNull Function<ResourceType, @Nullable RuntimeResourcePack> packFunction) {
    sidedCallback.register((type, resources) -> {
      final RuntimeResourcePack pack = packFunction.apply(type);
      if (pack != null) resources.add(pack);
    });
  }

  public void registerPacks(@NotNull Function<ResourceType, @NotNull Collection<RuntimeResourcePack>> packsFunction) {
    sidedCallback.register((type, resources) -> resources.addAll(packsFunction.apply(type)));
  }
}
