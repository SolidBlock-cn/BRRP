package pers.solid.brrp.fabric.mixin;

import net.devtech.arrp.BRRPDevelopment;
import net.fabricmc.api.ModInitializer;
import org.spongepowered.asm.mixin.Mixin;
import pers.solid.brrp.PlatformBridge;

@Mixin(BRRPDevelopment.class)
public class BRRPDevelopmentMixin implements ModInitializer {
  @Override
  public void onInitialize() {
    PlatformBridge.getInstance().onDevelopmentInitialize();
  }
}
