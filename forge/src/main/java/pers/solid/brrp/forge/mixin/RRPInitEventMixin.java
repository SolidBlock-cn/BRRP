package pers.solid.brrp.forge.mixin;

import net.devtech.arrp.api.RRPInitEvent;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RRPInitEvent.class)
public class RRPInitEventMixin implements IModBusEvent {
}
