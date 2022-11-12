package pers.solid.brrp.forge.mixin;

import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.common.extensions.IForgePackResources;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;

@ApiStatus.AvailableSince("0.8.1")
@Mixin(RuntimeResourcePackImpl.class)
public class RuntimeResourcePackImplMixin implements IForgePackResources {
  @Override
  public void init(ResourceType packType) {
    ((ResourcePack) this).getNamespaces(packType).forEach(this::initForNamespace);
  }
}
