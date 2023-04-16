package pers.solid.brrp.v1;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.mixin.IdentifierMixin;

/**
 * <p>This is a simple extension to {@link Identifier}, the injection of which is defined in {@code fabric.mod.json} and {@code architectury.common.json}.</p>
 * <p>If the interface is not injected because of low loom version or other possible reasons, don't worry. You can cast the identifier to this interface, as long as the mixin is loaded.</p>
 * <p>The method bodies in this interface are not directly used; they are overridden. However, in situations where mixins are not loaded, these methods are directly used.</p>
 *
 * @see IdentifierMixin
 */
public interface IdentifierExtension {
  /**
   * Append the string to the path of the identifier. The namespace will keep unchanged.
   *
   * @param suffix The string to be appended.
   * @return A new identifier.
   */
  @Contract("_ -> new")
  default Identifier brrp_suffixed(@NotNull String suffix) {
    Identifier identifier = (Identifier) this;
    return new Identifier(identifier.getNamespace(), identifier.getPath() + suffix);
  }

  /**
   * Prepend the string to the path of the identifier. The namespace will keep unchanged.
   *
   * @param prefix The string to be prepended.
   * @return A new identifier.
   * @see Identifier#withPrefixedPath(String)
   */
  @Contract("_ -> new")
  default Identifier brrp_prefixed(@NotNull String prefix) {
    Identifier identifier = (Identifier) this;
    return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
  }

  /**
   * Prepend and append to the path of the identifier at the same time. The namespace will keep unchanged.
   *
   * @param prefix The string to be prepended.
   * @param suffix The string to be appended.
   * @return A new identifier.
   */
  @Contract("_, _ -> new")
  default Identifier brrp_prefix_and_suffixed(@NotNull String prefix, @NotNull String suffix) {
    Identifier identifier = (Identifier) this;
    return new Identifier(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
  }
}
