package pers.solid.brrp.forge.mixin;

import net.devtech.arrp.api.RRPEvent;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RRPEvent.class)
public class RRPEventMixin implements IModBusEvent {
}
