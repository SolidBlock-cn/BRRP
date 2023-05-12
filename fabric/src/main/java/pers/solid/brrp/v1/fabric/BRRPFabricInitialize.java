package pers.solid.brrp.v1.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import pers.solid.brrp.v1.BRRPTest;
import pers.solid.brrp.v1.gui.RRPConfigScreen;

public class BRRPFabricInitialize implements ModInitializer {
  @Override
  public void onInitialize() {
    if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().getConfigDir().resolve("brrp-test-features.json").toFile().exists()) {
      BRRPTest.registerPacks();
    }
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
        dispatcher.register(ClientCommandManager.literal("brrp:config")
            .executes(context -> {
              final MinecraftClient client = context.getSource().getClient();
              client.send(() -> client.setScreen(new RRPConfigScreen(client.currentScreen)));
              return 1;
            }));
      });
    }
  }
}
