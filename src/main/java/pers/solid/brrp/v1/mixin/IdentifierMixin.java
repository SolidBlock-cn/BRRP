package pers.solid.brrp.v1.mixin;

import net.minecraft.util.Identifier;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.IdentifierExtension;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements IdentifierExtension {

  @Shadow
  @Final
  protected String namespace;

  @Shadow
  @Final
  protected String path;

  /**
   * {@inheritDoc}
   *
   * @param suffix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_suffixed(@Pattern("[a-z\\d/._-]+") @NotNull String suffix) {
    return new Identifier(namespace, path + suffix);
  }

  /**
   * {@inheritDoc}
   *
   * @param prefix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_prefixed(@Pattern("[a-z\\d/._-]+") @NotNull String prefix) {
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
  public Identifier brrp_prefix_and_suffixed(@Pattern("[a-z\\d/._-]+") @NotNull String prefix, @NotNull String suffix) {
    return new Identifier(namespace, prefix + path + suffix);
  }
}
