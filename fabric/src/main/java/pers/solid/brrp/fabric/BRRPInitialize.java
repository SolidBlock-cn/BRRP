package pers.solid.brrp.fabric;

import net.devtech.arrp.BRRPDevelopment;
import net.fabricmc.api.ModInitializer;
import pers.solid.brrp.PlatformBridge;

public class BRRPInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    if (PlatformBridge.getInstance().isDevelopmentEnvironment()) {
      BRRPDevelopment.registerPacks();
    }
  }
}
