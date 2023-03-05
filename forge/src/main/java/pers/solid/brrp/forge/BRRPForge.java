package pers.solid.brrp.forge;

import net.devtech.arrp.BRRPDevelopment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("better_runtime_resource_pack")
public class BRRPForge {
  public BRRPForge() {
    if (!FMLLoader.isProduction()) {
      FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> event.register(ForgeRegistries.Keys.BLOCKS, helper -> BRRPDevelopment.registerPacks()));
    }
  }
}
