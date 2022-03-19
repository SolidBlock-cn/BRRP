package net.devtech.arrp.mixin;

import net.devtech.arrp.IdentifierExtension;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements IdentifierExtension {
  @Shadow
  public abstract String getNamespace();

  @Shadow
  public abstract String getPath();

  @Override
  public Identifier append(String s) {
    return new Identifier(getNamespace(), getPath() + s);
  }

  @Override
  public Identifier prepend(String s) {
    return new Identifier(getNamespace(), s + getPath());
  }
}
