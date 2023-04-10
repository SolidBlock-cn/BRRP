package pers.solid.brrp.v1.forge;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeTagHandler;
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
  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  private PlatformBridgeImpl() {
  }

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }

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
    block.setRegistryName(identifier);
    ForgeRegistries.BLOCKS.register(block);
  }

  @Override
  public void registerItem(Identifier identifier, Item item) {
    item.setRegistryName(identifier);
    ForgeRegistries.ITEMS.register(item);
  }

  @Override
  public Tag.Identified<Block> createBlockTag(Identifier identifier) {
    return ForgeTagHandler.makeWrapperTag(ForgeRegistries.BLOCKS, identifier);
  }

  @Override
  public Tag.Identified<Item> createItemTag(Identifier identifier) {
    return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, identifier);
  }

  @Override
  public Tag.Identified<Fluid> createFluidTag(Identifier identifier) {
    return ForgeTagHandler.makeWrapperTag(ForgeRegistries.FLUIDS, identifier);
  }

  @Override
  public Tag.Identified<EntityType<?>> createEntityTypeTag(Identifier identifier) {
    return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENTITIES, identifier);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return !FMLEnvironment.production;
  }
}
