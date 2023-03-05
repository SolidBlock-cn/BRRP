package net.devtech.arrp.api.forge;

import net.devtech.arrp.api.RRPEvent;
import net.devtech.arrp.api.RRPEventHelper;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.AvailableSince("0.8.1")
public class RRPEventHelperImpl<E extends Event> extends RRPEventHelper {
  @SuppressWarnings("Convert2MethodRef")
  public static final RRPEventHelperImpl<RRPEvent.BeforeVanilla> BEFORE_VANILLA = new RRPEventHelperImpl<>(consumer -> (RRPEvent.BeforeVanilla event) -> consumer.accept(event));
  @SuppressWarnings("Convert2MethodRef")
  public static final RRPEventHelperImpl<RRPEvent.AfterVanilla> AFTER_VANILLA = new RRPEventHelperImpl<>(consumer -> (RRPEvent.AfterVanilla event) -> consumer.accept(event));

  @SuppressWarnings("Convert2MethodRef")
  public static final RRPEventHelperImpl<RRPEvent.BeforeUser> BEFORE_USER = new RRPEventHelperImpl<>(consumer -> (RRPEvent.BeforeUser event) -> consumer.accept(event));
  private final Function<Consumer<Event>, Consumer<E>> consumerTransformer;

  public RRPEventHelperImpl(Function<Consumer<Event>, Consumer<E>> consumerTransformer) {
    this.consumerTransformer = consumerTransformer;
  }

  public static RRPEventHelper getBeforeVanilla() {
    return BEFORE_VANILLA;
  }

  public static RRPEventHelper getAfterVanilla() {
    return AFTER_VANILLA;
  }

  @Override
  public void registerPack(RuntimeResourcePack pack) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(consumerTransformer.apply(event -> ((RRPEvent) event).addPack(pack)));
  }

  @Override
  public void registerSidedPack(ResourceType resourceType, RuntimeResourcePack pack) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(consumerTransformer.apply(event -> {
      if (((RRPEvent) event).resourceType == resourceType) ((RRPEvent) event).addPack(pack);
    }));
  }

  @Override
  public void registerPack(@NotNull Function<ResourceType, @Nullable RuntimeResourcePack> packFunction) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(consumerTransformer.apply(event -> {
      final RuntimeResourcePack pack = packFunction.apply(((RRPEvent) event).resourceType);
      if (pack != null) ((RRPEvent) event).addPack(pack);
    }));
  }

  @Override
  public void registerPacks(@NotNull Function<ResourceType, @NotNull Collection<RuntimeResourcePack>> packsFunction) {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(consumerTransformer.apply(event -> ((RRPEvent) event).addPacks(packsFunction.apply(((RRPEvent) event).resourceType))));
  }

  @ApiStatus.Experimental
  @ApiStatus.Internal
  public static RRPEventHelper getBeforeUser() {
    return BEFORE_USER;
  }
}
