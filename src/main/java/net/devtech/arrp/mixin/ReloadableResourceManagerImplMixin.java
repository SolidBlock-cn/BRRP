package net.devtech.arrp.mixin;

import net.devtech.arrp.api.RRPCallback;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin (LifecycledResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReloadableResourceManagerImpl.class);

	@ModifyVariable(method = "<init>",
			at = @At (value = "HEAD"),index = 2, argsOnly = true)
	private static List registerARRPs(List value) {
		LOGGER.info("ARRP register - before vanilla");
		List<ResourcePack> pack = new ArrayList<>();
		RRPCallback.BEFORE_VANILLA.invoker().insert(pack);
    pack.addAll(value);
    LOGGER.info("ARRP register - after vanilla");
    List<ResourcePack> pack2 = new ArrayList<>();
    RRPCallback.AFTER_VANILLA.invoker().insert(pack2);
    pack.addAll(pack2);
    return pack;
  }
}