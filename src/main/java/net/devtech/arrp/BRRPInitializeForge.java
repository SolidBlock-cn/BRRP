package net.devtech.arrp;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("better_runtime_resource_pack")
public class BRRPInitializeForge {
  public BRRPInitializeForge() {
    if (!FMLEnvironment.production) {
      FMLJavaModLoadingContext.get().getModEventBus().register(BRRPInitializeForge.class);
    }
  }

  @SubscribeEvent
  public static void onInitialize(RegisterEvent event) {
    event.register(ForgeRegistries.Keys.BLOCKS, helper -> BRRPDevelopment.onInitialize());
  }
}
