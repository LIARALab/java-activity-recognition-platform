package org.liara.api.realtime;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationHandlerMappingFactory
{
  @Bean
  @Autowired
  public @NonNull HandlerMapping getHandlerMapping (@NonNull final WebSocketHandler handler) {
    @NonNull final Map<@NonNull String, @NonNull WebSocketHandler> map = new HashMap<>();
    map.put("/events", handler);

    @NonNull final SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
    handlerMapping.setOrder(1);
    handlerMapping.setUrlMap(map);

    return handlerMapping;
  }

  @Bean
  public @NonNull WebSocketHandlerAdapter handlerAdapter () {
    return new WebSocketHandlerAdapter();
  }
}
