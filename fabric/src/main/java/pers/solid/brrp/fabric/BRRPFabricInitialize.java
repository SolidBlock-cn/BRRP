package pers.solid.brrp.fabric;

import net.devtech.arrp.BRRPDevelopment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class BRRPFabricInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
      BRRPDevelopment.registerPacks();
    }
  }
}
