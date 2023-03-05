package pers.solid.brrp.forge;

import net.devtech.arrp.BRRPDevelopment;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("better_runtime_resource_pack")
public class BRRPForge {
  public BRRPForge() {
    if (!FMLLoader.isProduction()) {
      FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, (RegistryEvent<Block> event) -> BRRPDevelopment.registerPacks());
    }
  }
}
