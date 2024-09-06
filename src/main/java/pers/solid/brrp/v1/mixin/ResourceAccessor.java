package pers.solid.brrp.v1.mixin;

import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.InputStream;

@Mixin(Resource.class)
public interface ResourceAccessor {
  @Accessor
  InputSupplier<InputStream> getInputSupplier();
}
