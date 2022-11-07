package net.devtech.arrp;

import org.jetbrains.annotations.Blocking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.PlatformBridge;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ARRP {
  public static List<Future<?>> futures;

  public static final Logger LOGGER = LoggerFactory.getLogger("BRRP (branch of ARRP)");

  public static void bridgePrelaunch() {
    PlatformBridge.getInstance().prelaunch();
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
