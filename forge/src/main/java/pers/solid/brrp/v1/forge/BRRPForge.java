package pers.solid.brrp.v1.forge;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import pers.solid.brrp.v1.BRRPTest;
import pers.solid.brrp.v1.gui.RRPConfigScreen;

@Mod("brrp_v1")
public class BRRPForge {
  public BRRPForge() {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BRRPForge::registerConfigScreen);
    if (!FMLLoader.isProduction() && FMLPaths.CONFIGDIR.get().resolve("brrp-test-features.json").toFile().exists()) {
      FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, (RegistryEvent<Block> event) -> BRRPTest.registerPacks());
    }
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerConfigScreen() {
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraftClient, screen) -> new RRPConfigScreen(screen));
  }
}
