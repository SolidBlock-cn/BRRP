package pers.solid.brrp.v1.forge;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.event.RegistryEvent;
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
      FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, (RegistryEvent<Block> event) ->  BRRPTest.registerPacks());
    }
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerConfigScreen() {
    ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, Suppliers.ofInstance(new ConfigGuiHandler.ConfigGuiFactory((minecraftClient, screen) -> new RRPConfigScreen(screen))));
  }
}
