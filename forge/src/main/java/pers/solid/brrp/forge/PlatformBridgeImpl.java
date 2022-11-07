package pers.solid.brrp.forge;

import net.devtech.arrp.BRRPDevelopment;
import net.devtech.arrp.api.RRPCallbackForge;
import net.devtech.arrp.api.RRPEvent;
import net.devtech.arrp.api.RRPInitEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.PlatformBridge;

import java.nio.file.FileSystems;
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
  public void prelaunch() {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::particleFactoryRegister));
  }

  @SuppressWarnings("deprecation")
  @OnlyIn(Dist.CLIENT)
  private void particleFactoryRegister(ParticleFactoryRegisterEvent event) {
    if (FMLEnvironment.dist.isClient()) {
      ModLoader.get().postEvent(((Event & IModBusEvent) new RRPInitEvent()));
    }
  }

  @Override
  public void onDevelopmentInitialize() {
    FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, (RegistryEvent.Register<Block> event) -> BRRPDevelopment.registerPacks());
  }

  @Override
  public Path getConfigDir() {
    return FileSystems.getDefault().getPath(FMLConfig.defaultConfigPath());
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

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }
}
