package pers.solid.brrp.v1;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BRRPMixins {
  /**
   * The logger used for mixins of this mod.
   */
  @ApiStatus.Internal
  public static final Logger LOGGER = LoggerFactory.getLogger("BRRP/Mixin");

  private BRRPMixins() {
  }
}
