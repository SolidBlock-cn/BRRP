package net.devtech.arrp;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>This is a simple extension to {@link Identifier}, the injection of which is defined in {@code fabric.mod.json}.</p>
 * <p>If the interface is not injected because of low loom version or other possible reasons, don't worry. You can cast the identifier to this interface, as long as the mixin is loaded.</p>
 * <p>The method bodies in this interface are not directly used; they are overridden. However, in situations where mixins are not loaded, these methods are directly used.</p>
 *
 * @see net.devtech.arrp.mixin.IdentifierMixin
 */
public interface IdentifierExtension {
  /**
   * Append the string to the path of the identifier. The namespace will keep unchanged.
   *
   * @param s The string to be appended.
   * @return A new identifier.
   */
  @Contract("_ -> new")
  default Identifier brrp_append(@NotNull String s) {
    Identifier identifier = (Identifier) this;
    return new Identifier(identifier.getNamespace(), identifier.getPath() + s);
  }

  /**
   * Prepend the string to the path of the identifier. The namespace will keep unchanged.
   *
   * @param s The string to be prepended.
   * @return A new identifier.
   */
  @Contract("_ -> new")
  default Identifier brrp_prepend(@NotNull String s) {
    Identifier identifier = (Identifier) this;
    return new Identifier(identifier.getNamespace(), s + identifier.getPath());
  }
}
