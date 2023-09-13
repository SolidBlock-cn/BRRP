package pers.solid.brrp.v1.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import pers.solid.brrp.v1.BRRPTest;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.RRPEventHelper;

public class BRRPFabricInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    PlatformBridge.__instance = PlatformBridgeImpl.INSTANCE;
    RRPEventHelper.BEFORE_USER = RRPEventHelperImpl.BEFORE_USER;
    RRPEventHelper.BEFORE_VANILLA = RRPEventHelperImpl.BEFORE_VANILLA;
    RRPEventHelper.AFTER_VANILLA = RRPEventHelperImpl.AFTER_VANILLA;
    if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().getConfigDir().resolve("brrp-test-features.json").toFile().exists()) {
      BRRPTest.registerPacks();
    }
  }
}
