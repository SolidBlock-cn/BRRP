package pers.solid.brrp.v1.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import pers.solid.brrp.v1.gui.RRPConfigScreen;

@Environment(EnvType.CLIENT)
public class RRPModMenu implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return RRPConfigScreen::new;
  }
}
