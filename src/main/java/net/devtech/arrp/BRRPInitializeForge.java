package net.devtech.arrp;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("better_runtime_resource_pack")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BRRPInitializeForge {

  public BRRPInitializeForge() {
  }

  @SubscribeEvent
  public static void onInitialize(RegisterEvent event) {
    if (!FMLEnvironment.production) {
      ARRP.LOGGER.info("BRRP development registering objects!");
      event.register(ForgeRegistries.Keys.BLOCKS, helper -> BRRPDevelopment.onInitialize());
    }
  }
}
