package pers.solid.brrp.v1.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pers.solid.brrp.v1.impl.RegistryInfoGetterExtension;

@Mixin(targets = "net/minecraft/registry/RegistryLoader$1")
public abstract class RegistryInfoGetterAyMixin implements RegistryInfoGetterExtension {
  @Unique
  private DynamicRegistryManager baseRegistryManager$brrp;

  @Override
  public DynamicRegistryManager getBaseRegistryManager$brrp() {
    return baseRegistryManager$brrp;
  }

  @Override
  public void setBaseRegistryManager$brrp(DynamicRegistryManager wrapperLookup) {
    this.baseRegistryManager$brrp = wrapperLookup;
  }
}
