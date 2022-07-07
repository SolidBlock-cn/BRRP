package net.devtech.arrp.api;

/**
 * This is the entrypoint called on preLaunch <i>asynchronously</i>.
 */
public interface RRPPreGenEntrypoint {
  /**
   * Generate assets in this method and put them in a runtime resource pack. Don't forget to register the runtime resource pack in your {@link RRPCallback} or {@link RRPCallbackForge}.
   * <p>
   * Note that it happens on prelaunch and at that time most classes are not loaded.
   *
   * @see RRPCallback
   */
  void pregen();
}
