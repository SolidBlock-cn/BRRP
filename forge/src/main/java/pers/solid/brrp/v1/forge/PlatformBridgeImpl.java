package pers.solid.brrp.v1.forge;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.v1.PlatformBridge;

import java.nio.file.Path;
import java.util.List;

public class PlatformBridgeImpl extends PlatformBridge {
  private PlatformBridgeImpl() {
  }

  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.BeforeVanilla beforeVanilla = new RRPEvent.BeforeVanilla(packs, type);
    if (ModLoader.isLoadingStateValid()) {
      ModLoader.get().postEvent((Event & IModBusEvent) beforeVanilla);
    }
  }

  @ApiStatus.Experimental
  @Override
  public void postBeforeUser(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.BeforeUser beforeUser = new RRPEvent.BeforeUser(packs, type);
    if (ModLoader.isLoadingStateValid()) {
      ModLoader.get().postEvent((Event & IModBusEvent) beforeUser);
    }
  }

  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.AfterVanilla afterVanilla = new RRPEvent.AfterVanilla(packs, type);
    if (ModLoader.isLoadingStateValid()) {
      ModLoader.get().postEvent((Event & IModBusEvent) afterVanilla);
    }
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
