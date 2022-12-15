package net.devtech.arrp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Blocking;
import pers.solid.brrp.PlatformBridge;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The entrypoint for pregen resource generation. In Fabric, the class implements {@code PreLaunchEntrypoint} through {@code ARRPMixin} in the Fabric submodule.
 */
public class ARRP {
  public static List<Future<?>> futures;

  public static final Logger LOGGER = LogManager.getLogger("BRRP");

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
