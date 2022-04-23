package net.devtech.arrp.mixin;

import net.devtech.arrp.IdentifierExtension;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements IdentifierExtension {
  @Shadow
  public abstract String getNamespace();

  @Shadow
  public abstract String getPath();

  /**
   * {@inheritDoc}
   *
   * @param s {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_append(@NotNull String s) {
    return new Identifier(getNamespace(), getPath() + s);
  }

  /**
   * {@inheritDoc}
   *
   * @param s {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_prepend(@NotNull String s) {
    return new Identifier(getNamespace(), s + getPath());
  }
}
