package net.devtech.arrp;

import net.minecraft.util.Identifier;

/**
 * This is a simple extension to {@link Identifier}, the injection of which is defined in {@code fabric.mod.json}.
 *
 * @see net.devtech.arrp.mixin.IdentifierMixin
 */
public interface IdentifierExtension {
  default Identifier brrp_append(String s) {
    throw new AssertionError();
  }

  default Identifier brrp_prepend(String s) {
    throw new AssertionError();
  }
}
