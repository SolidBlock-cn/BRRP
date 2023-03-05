package net.devtech.arrp;

import org.jetbrains.annotations.Blocking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The entrypoint for pregen resource generation.
 */
public class ARRP {
  public static List<Future<?>> futures;

  public static final Logger LOGGER = LoggerFactory.getLogger("BRRP");

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
