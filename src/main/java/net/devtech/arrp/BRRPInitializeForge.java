package net.devtech.arrp;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("better_runtime_resource_pack")
public class BRRPInitializeForge {
  public BRRPInitializeForge() {
    if (!FMLEnvironment.production) {
      FMLJavaModLoadingContext.get().getModEventBus().register(BRRPInitializeForge.class);
    }
  }

  @SubscribeEvent
  public static void onInitialize(RegistryEvent.Register<Block> event) {
    BRRPDevelopment.onInitialize();
  }
}
