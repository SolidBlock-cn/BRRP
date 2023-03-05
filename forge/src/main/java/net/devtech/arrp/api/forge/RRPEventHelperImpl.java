package net.devtech.arrp.api.forge;

import net.devtech.arrp.api.RRPEvent;
import net.devtech.arrp.api.RRPEventHelper;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

@ApiStatus.AvailableSince("0.8.1")
public class RRPEventHelperImpl<E extends RRPEvent> extends RRPEventHelper {
  public static final RRPEventHelperImpl<RRPEvent.BeforeVanilla> BEFORE_VANILLA = new RRPEventHelperImpl<>();
  public static final RRPEventHelperImpl<RRPEvent.AfterVanilla> AFTER_VANILLA = new RRPEventHelperImpl<>();
  public static final RRPEventHelperImpl<RRPEvent.BeforeUser> BEFORE_USER = new RRPEventHelperImpl<>();

  public static RRPEventHelper getBeforeVanilla() {
    return BEFORE_VANILLA;
  }

  public static RRPEventHelper getAfterVanilla() {
    return AFTER_VANILLA;
  }

  @Override
  public void registerPack(RuntimeResourcePack pack) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener((E event) -> event.addPack(pack));
  }

  @Override
  public void registerSidedPack(ResourceType resourceType, RuntimeResourcePack pack) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener((E event) -> {
      if (event.resourceType == resourceType) event.addPack(pack);
    });
  }

  @Override
  public void registerPack(@NotNull Function<ResourceType, @Nullable RuntimeResourcePack> packFunction) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener((E event) -> {
      final RuntimeResourcePack pack = packFunction.apply(event.resourceType);
      if (pack != null) event.addPack(pack);
    });
  }

  @Override
  public void registerPacks(@NotNull Function<ResourceType, @NotNull Collection<RuntimeResourcePack>> packsFunction) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener((E event) -> event.addPacks(packsFunction.apply(event.resourceType)));
  }

  @ApiStatus.Experimental
  @ApiStatus.Internal
  public static RRPEventHelper getBeforeUser() {
    return BEFORE_USER;
  }
}
