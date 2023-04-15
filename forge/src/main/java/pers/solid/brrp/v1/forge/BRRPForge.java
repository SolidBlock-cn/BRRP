package pers.solid.brrp.v1.forge;

import com.google.common.base.Suppliers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import pers.solid.brrp.v1.BRRPTest;
import pers.solid.brrp.v1.gui.RRPConfigScreen;

@Mod("brrp_v1")
public class BRRPForge {
  public BRRPForge() {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BRRPForge::registerConfigScreen);
    if (!FMLLoader.isProduction() && FMLPaths.CONFIGDIR.get().resolve("brrp-test-features.json").toFile().exists()) {
      FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> event.register(ForgeRegistries.Keys.BLOCKS, helper -> BRRPTest.registerPacks()));
    }
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerConfigScreen() {
    ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, Suppliers.ofInstance(new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> new RRPConfigScreen(screen))));
  }
}
