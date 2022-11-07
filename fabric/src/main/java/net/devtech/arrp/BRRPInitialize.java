package net.devtech.arrp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class BRRPInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    final FabricLoader instance = FabricLoader.getInstance();
    if (instance.isDevelopmentEnvironment()) {
      instance.getEntrypoints("brrp:develop", ModInitializer.class).forEach(ModInitializer::onInitialize);
    }
  }
}
