package pers.solid.brrp.v1.util;

/**
 * @see Runnable
 */
public interface FailableRunnable<E extends Throwable> {
  void run() throws E;
}
