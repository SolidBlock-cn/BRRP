package pers.solid.brrp.v1.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import pers.solid.brrp.v1.BRRPTest;

public class BRRPFabricInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().getConfigDir().resolve("brrp-test-features.json").toFile().exists()) {
      BRRPTest.registerPacks();
    }
  }
}
