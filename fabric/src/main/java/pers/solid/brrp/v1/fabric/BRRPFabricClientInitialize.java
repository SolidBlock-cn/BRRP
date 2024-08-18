package pers.solid.brrp.v1.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import pers.solid.brrp.v1.gui.RRPConfigScreen;

@Environment(EnvType.CLIENT)
public class BRRPFabricClientInitialize implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
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
