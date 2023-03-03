package pers.solid.brrp.fabric;

import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class BRRPPrelaunch implements PreLaunchEntrypoint {
  @Override
  public void onPreLaunch() {
    ARRP.LOGGER.info("BRRP data generation: PreLaunch");
    FabricLoader loader = FabricLoader.getInstance();
    List<Future<?>> futures = new ArrayList<>();
    for (RRPPreGenEntrypoint entrypoint : loader.getEntrypoints("rrp:pregen", RRPPreGenEntrypoint.class)) {
      futures.add(RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(entrypoint::pregen));
    }
    ARRP.futures = futures;
  }
}
