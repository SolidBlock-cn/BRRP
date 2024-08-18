package pers.solid.brrp.v1.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.RRPEventHelper;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;
import pers.solid.brrp.v1.fabric.api.SidedRRPCallback;

import java.util.Collection;
import java.util.function.Function;

public class RRPEventHelperImpl extends RRPEventHelper {
  public static final RRPEventHelperImpl BEFORE_VANILLA = new RRPEventHelperImpl(RRPCallback.BEFORE_VANILLA, SidedRRPCallback.BEFORE_VANILLA);
  public static final RRPEventHelperImpl AFTER_VANILLA = new RRPEventHelperImpl(RRPCallback.AFTER_VANILLA, SidedRRPCallback.AFTER_VANILLA);
  public static final RRPEventHelperImpl BEFORE_USER = new RRPEventHelperImpl(RRPCallback.BEFORE_USER, SidedRRPCallback.BEFORE_USER);
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

  @ApiStatus.Internal
  public static RRPEventHelper getBeforeUser() {
    return BEFORE_USER;
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
