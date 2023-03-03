package pers.solid.brrp.fabric;

import net.devtech.arrp.BRRPDevelopment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class BRRPInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    final FabricLoader instance = FabricLoader.getInstance();
    if (instance.isDevelopmentEnvironment()) {
      BRRPDevelopment.registerPacks();
    }
  }
}
