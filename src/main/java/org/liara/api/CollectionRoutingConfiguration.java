package org.liara.api;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionControllerRoutingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@EnableWebFlux
public class CollectionRoutingConfiguration
  implements WebFluxConfigurer
{
  @NonNull
  private final CollectionControllerRoutingFactory _factory;

  @Autowired
  public CollectionRoutingConfiguration (@NonNull final CollectionControllerRoutingFactory factory) {
    _factory = factory;
  }

  @Bean("restCollectionRoutingFunction")
  public @NonNull RouterFunction<ServerResponse> getRestCollectionRoutingFunction () {
    return _factory.get();
  }

  @Override
  public void configurePathMatching (@NonNull final PathMatchConfigurer configurer) {
    configurer.setUseCaseSensitiveMatch(false).setUseTrailingSlashMatch(true);
  }
}
