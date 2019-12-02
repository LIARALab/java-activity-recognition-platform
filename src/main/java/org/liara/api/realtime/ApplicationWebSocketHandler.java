package org.liara.api.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationWebSocketHandler
  implements WebSocketHandler
{
  @NonNull
  private final ApplicationEventFlux _eventFlux;

  @NonNull
  private final ObjectMapper _mapper;

  @Autowired
  public ApplicationWebSocketHandler (
    @NonNull final ObjectMapper mapper,
    @NonNull final ApplicationEventFlux eventFlux
  ) {
    _mapper = mapper;
    _eventFlux = eventFlux;
  }

  @Override
  public @NonNull Mono<@NonNull Void> handle (@NonNull final WebSocketSession session) {
    Logger.getLogger(getClass().getName()).info("Handle " + session);
    return session.send(
      _eventFlux.getFlux()
                .map(this::eventToMessage)
                .map(session::textMessage)
    );
  }

  private @NonNull String eventToMessage (@NonNull final ApplicationEvent object) {
    try {
      return _mapper.writeValueAsString(object);
    } catch (@NonNull final Throwable exception) {
      throw new Error(
        "Unable to write the given event of type " + object.getClass().getName() +
        " as a JSON string.", exception
      );
    }
  }
}
