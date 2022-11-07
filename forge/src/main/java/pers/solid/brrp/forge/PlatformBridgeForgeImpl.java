package pers.solid.brrp.forge;

import net.devtech.arrp.BRRPDevelopment;
import net.devtech.arrp.api.forge.RRPEvent;
import net.devtech.arrp.api.forge.RRPInitEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import pers.solid.brrp.PlatformBridge;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class PlatformBridgeForgeImpl extends PlatformBridge {
  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.BeforeVanilla beforeVanilla = new RRPEvent.BeforeVanilla(packs, type);
    ModLoader.get().postEvent(beforeVanilla);
  }

  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    RRPEvent.AfterVanilla afterVanilla = new RRPEvent.AfterVanilla(packs, type);
    ModLoader.get().postEvent(afterVanilla);
  }

  @Override
  public void prelaunch() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::particleFactoryRegister);
  }

  private void particleFactoryRegister(RegisterParticleProvidersEvent event) {
    if (FMLEnvironment.dist.isClient()) {
      ModLoader.get().postEvent(new RRPInitEvent());
    }
  }

  @Override
  public void onDevelopmentInitialize() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
      event.register(ForgeRegistries.Keys.BLOCKS, helper -> {
        Objects.requireNonNull(BRRPDevelopment.LAVA_BLOCK);
      });
    });
    FMLJavaModLoadingContext.get().getModEventBus().addListener((RRPEvent.BeforeVanilla event) -> event.addPack(BRRPDevelopment.refreshPack(event.resourceType)));
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
}
