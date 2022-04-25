package net.devtech.arrp.mixin;

import net.devtech.arrp.IdentifierExtension;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
   * @param prefix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_append(@NotNull String prefix) {
    return new Identifier(namespace, path + prefix);
  }

  /**
   * {@inheritDoc}
   *
   * @param suffix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_prepend(@NotNull String suffix) {
    return new Identifier(namespace, suffix + path);
  }

  /**
   * {@inheritDoc}
   *
   * @param prefix {@inheritDoc}
   * @param suffix {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public Identifier brrp_pend(@NotNull String prefix, @NotNull String suffix) {
    return new Identifier(namespace, prefix + path + suffix);
  }
}
