package pers.solid.brrp.forge;

import net.devtech.arrp.api.RRPCallbackForge;
import net.devtech.arrp.api.RRPEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.PlatformBridge;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@ApiStatus.AvailableSince("0.8.1")
public class PlatformBridgeImpl extends PlatformBridge {
  private PlatformBridgeImpl() {
  }

  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  @SuppressWarnings("deprecation")
  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.BeforeVanilla beforeVanilla = new RRPEvent.BeforeVanilla(packs, type);
    if (ModLoader.isLoadingStateValid()) {
      ModLoader.get().postEvent((Event & IModBusEvent) beforeVanilla);
    }

    // This is already deprecated.
    RRPCallbackForge.BEFORE_VANILLA.build().stream().map(f -> f.apply(type)).filter(Objects::nonNull).forEach(packs::add);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.AfterVanilla afterVanilla = new RRPEvent.AfterVanilla(packs, type);
    if (ModLoader.isLoadingStateValid()) {
      ModLoader.get().postEvent((Event & IModBusEvent) afterVanilla);
    }

    // This is already deprecated.
    RRPCallbackForge.AFTER_VANILLA.build().stream().map(f -> f.apply(type)).filter(Objects::nonNull).forEach(packs::add);
  }

  @Override
  public Path getConfigDir() {
    return Path.of(FMLConfig.defaultConfigPath());
  }

  @Override
  public boolean isClientEnvironment() {
    return FMLEnvironment.dist == Dist.CLIENT;
  }

  @Override
  public void registerBlock(Identifier identifier, Block block) {
    ForgeRegistries.BLOCKS.register(identifier, block);
  }

  @Override
  public void registerItem(Identifier identifier, Item item) {
    ForgeRegistries.ITEMS.register(identifier, item);
  }

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return !FMLEnvironment.production;
  }
}
