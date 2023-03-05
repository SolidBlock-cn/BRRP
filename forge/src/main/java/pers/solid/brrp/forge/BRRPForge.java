package pers.solid.brrp.forge;

import net.devtech.arrp.BRRPDevelopment;
import net.devtech.arrp.api.RRPInitEvent;
import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("better_runtime_resource_pack")
public class BRRPForge {
  public BRRPForge() {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::particleFactoryRegister));
    if (!FMLLoader.isProduction()) {
      FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, (RegistryEvent<Block> event) -> BRRPDevelopment.registerPacks());
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void particleFactoryRegister(ParticleFactoryRegisterEvent event) {
    if (FMLEnvironment.dist.isClient()) {
      ModLoader.get().postEvent(new RRPInitEvent());
    }
  }
}
