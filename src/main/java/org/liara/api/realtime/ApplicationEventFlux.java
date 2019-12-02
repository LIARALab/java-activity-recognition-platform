package org.liara.api.realtime;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.event.event.DidConsumeEvent;
import org.liara.api.event.event.DidRejectEvent;
import org.liara.api.event.event.WillConsumeEvent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.logging.Logger;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApplicationEventFlux
{
  @NonNull
  private final ConnectableFlux<@NonNull ApplicationEvent> _flux;
  @NonNull
  private       FluxSink<@NonNull ApplicationEvent>        _sink;

  public ApplicationEventFlux () {
    _flux = Flux.create((@NonNull final FluxSink<@NonNull ApplicationEvent> sink) -> {
      _sink = sink;
    }).publish();

    _flux.connect();
  }

  @EventListener
  public void handleWillConsumeEvent (@NonNull final WillConsumeEvent event) {
    Logger.getLogger(getClass().getName()).info("Emitting " + event);
    _sink.next(event);
  }

  @EventListener
  public void handleDidConsumeEvent (@NonNull final DidConsumeEvent event) {
    Logger.getLogger(getClass().getName()).info("Emitting " + event);
    _sink.next(event);
  }

  @EventListener
  public void handleDidRejectEvent (@NonNull final DidRejectEvent event) {
    Logger.getLogger(getClass().getName()).info("Emitting " + event);
    _sink.next(event);
  }

  public @NonNull ConnectableFlux<@NonNull ApplicationEvent> getFlux () {
    return _flux;
  }
}
