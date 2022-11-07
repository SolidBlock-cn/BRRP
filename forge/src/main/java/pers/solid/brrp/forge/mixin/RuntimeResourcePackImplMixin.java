package pers.solid.brrp.forge.mixin;

import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.common.extensions.IForgePackResources;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RuntimeResourcePackImpl.class)
public class RuntimeResourcePackImplMixin implements IForgePackResources {
  @Override
  public void init(ResourceType packType) {
    ((ResourcePack) this).getNamespaces(packType).forEach(this::initForNamespace);
  }
}
