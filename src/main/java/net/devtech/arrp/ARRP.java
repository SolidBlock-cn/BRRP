package net.devtech.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.devtech.arrp.api.SidedRRPCallback;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Blocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ARRP implements PreLaunchEntrypoint {
  public static final Logger LOGGER = LogManager.getLogger("BRRP (branch of ARRP)");
  private static List<Future<?>> futures;

  @Override
  public void onPreLaunch() {
    SidedRRPCallback.BEFORE_VANILLA.register((type, resources) -> RRPCallback.BEFORE_VANILLA.invoker().insert(resources));
    SidedRRPCallback.AFTER_VANILLA.register((type, resources) -> RRPCallback.AFTER_VANILLA.invoker().insert(resources));
    LOGGER.info("BRRP data generation: PreLaunch");
    FabricLoader loader = FabricLoader.getInstance();
    List<Future<?>> futures = new ArrayList<>();
    for (RRPPreGenEntrypoint entrypoint : loader.getEntrypoints("rrp:pregen", RRPPreGenEntrypoint.class)) {
      futures.add(RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(entrypoint::pregen));
    }
    ARRP.futures = futures;
  }

  @Blocking
  public static void waitForPregen() throws ExecutionException, InterruptedException {
    if (futures != null) {
      for (Future<?> future : futures) {
        future.get();
      }
      futures = null;
    }
  }
}
