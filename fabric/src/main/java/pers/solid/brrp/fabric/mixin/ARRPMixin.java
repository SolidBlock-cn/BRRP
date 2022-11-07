package pers.solid.brrp.fabric.mixin;

import net.devtech.arrp.ARRP;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ARRP.class)
public class ARRPMixin implements PreLaunchEntrypoint {
  @Override
  public void onPreLaunch() {
    ARRP.bridgePrelaunch();
  }
}
