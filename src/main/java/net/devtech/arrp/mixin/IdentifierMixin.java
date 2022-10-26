package net.devtech.arrp.mixin;

import net.devtech.arrp.IdentifierExtension;
import net.minecraft.util.Identifier;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements IdentifierExtension {

  @Shadow
  @Final
  private String namespace;

  @Shadow
  @Final
  private String path;

  /**
   * {@inheritDoc}
   *
   * @param suffix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_append(@Pattern("[a-z\\d/._-]+") @NotNull String suffix) {
    return new Identifier(namespace, path + suffix);
  }

  /**
   * {@inheritDoc}
   *
   * @param prefix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_prepend(@Pattern("[a-z\\d/._-]+") @NotNull String prefix) {
    return new Identifier(namespace, prefix + path);
  }

  /**
   * {@inheritDoc}
   *
   * @param prefix {@inheritDoc}
   * @param suffix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_pend(@Pattern("[a-z\\d/._-]+") @NotNull String prefix, @NotNull String suffix) {
    return new Identifier(namespace, prefix + path + suffix);
  }
}
