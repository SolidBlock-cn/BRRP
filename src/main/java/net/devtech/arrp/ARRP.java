package net.devtech.arrp;

import net.devtech.arrp.api.RRPPreGenEvent;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Blocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class ARRP {
  public static final Logger LOGGER = LogManager.getLogger("BRRP");
  private static List<Future<?>> futures;

  public void onPreLaunch() {
    LOGGER.info("BRRP data generation: PreLaunch");
    List<Future<?>> futures = new ArrayList<>();
    MinecraftForge.EVENT_BUS.post(new RRPPreGenEvent(), (listener, event) -> futures.add(RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(() -> listener.invoke(event))));
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
