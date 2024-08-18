package pers.solid.brrp.v1.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ForwardingEvent<T1, T2> extends Event<T1> {
  public final Function<T1, T2> forwardingFunction;
  public final Event<T2> forwardedEvent;

  public ForwardingEvent(Function<T1, T2> forwardingFunction, Event<T2> forwardedEvent, Function<T2, T1> invokerFunction) {
    this.forwardingFunction = forwardingFunction;
    this.forwardedEvent = forwardedEvent;
    invoker = invokerFunction.apply(forwardedEvent.invoker());
  }

  @Override
  public void register(T1 listener) {
    forwardedEvent.register(forwardingFunction.apply(listener));
  }

  @Override
  public void register(Identifier phase, T1 listener) {
    forwardedEvent.register(phase, forwardingFunction.apply(listener));
  }

  @Override
  public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
    forwardedEvent.addPhaseOrdering(firstPhase, secondPhase);
  }
}
