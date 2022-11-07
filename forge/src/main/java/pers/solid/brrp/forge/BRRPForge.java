package pers.solid.brrp.forge;

import net.devtech.arrp.ARRP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("better_runtime_resource_pack")
public class BRRPForge {
  public BRRPForge() {
    ARRP.bridgePrelaunch();
    if (!FMLLoader.isProduction()) {
      PlatformBridgeImpl.getInstance().onDevelopmentInitialize();
    }
  }
}
