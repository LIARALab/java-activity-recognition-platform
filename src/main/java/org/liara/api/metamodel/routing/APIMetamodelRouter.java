package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.collection.CollectionController;
import org.liara.api.metamodel.collection.HttpCollectionControllerHandler;
import org.liara.api.metamodel.collection.HttpCollectionControllerHandlerFactory;
import org.liara.api.metamodel.collection.RootCollectionController;
import org.liara.api.metamodel.model.HttpModelControllerHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class APIMetamodelRouter
  implements RouterFunction
{
  @NonNull
  private static final Predicate<String> IS_LONG = Pattern.compile("^[0-9]+$").asPredicate();

  @NonNull
  private static final Predicate<String> IS_UUID4 = Pattern.compile("^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0" +
                                                                    "-9A-F]{3}-[0-9A-F]{12}$",
    Pattern.CASE_INSENSITIVE
  ).asPredicate();

  @NonNull
  private final ApplicationContext _applicationContext;

  @Autowired
  public APIMetamodelRouter (@NonNull final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
  }

  @Override
  public @NonNull Mono<HandlerFunction> route (@NonNull final ServerRequest request) {
    @NonNull final PathNavigator pathNavigator = new PathNavigator(request);
    @NonNull MetamodelRoutingResult result     = EmptyMetamodelRoute.getInstance();

    while (pathNavigator.hasNext()) {
      if (InvalidMetamodelRoute.is(result)) {
        return Mono.empty();
      }

      result = route(result, pathNavigator.next());
    }

    return resolveRoute(result, request);
  }

  private @NonNull Mono<HandlerFunction> resolveRoute (
    @NonNull final MetamodelRoutingResult result, @NonNull final ServerRequest request
  )
  {
    if (CollectionMetamodelRoute.is(result)) {
      return resolveCollectionRoute((CollectionMetamodelRoute) result, request);
    }

    if (ModelMetamodelRoute.is(result)) {
      return resolveModelRoute((ModelMetamodelRoute) result, request);
    }

    return Mono.empty();
  }

  private @NonNull Mono<HandlerFunction> resolveModelRoute (
    @NonNull final ModelMetamodelRoute result, @NonNull final ServerRequest request
  )
  {
    if (request.method() == HttpMethod.GET) return Mono.just(result::get);
    if (request.method() == HttpMethod.PATCH) return Mono.just(result::patch);
    if (request.method() == HttpMethod.PUT) return Mono.just(result::put);
    if (request.method() == HttpMethod.DELETE) return Mono.just(result::delete);

    return Mono.empty();
  }

  private @NonNull Mono<HandlerFunction> resolveCollectionRoute (
    @NonNull final CollectionMetamodelRoute result, @NonNull final ServerRequest request
  )
  {
    @NonNull final HttpCollectionControllerHandler handler = HttpCollectionControllerHandlerFactory.getHandlerFor(result
                                                                                                                    .getCollectionController());

    if (request.method() == HttpMethod.GET) return Mono.just(handler::get);
    if (request.method() == HttpMethod.PATCH) return Mono.just(handler::patch);
    if (request.method() == HttpMethod.PUT) return Mono.just(handler::put);
    if (request.method() == HttpMethod.POST) return Mono.just(handler::post);
    if (request.method() == HttpMethod.DELETE) return Mono.just(handler::delete);

    return Mono.empty();
  }

  private @NonNull MetamodelRoutingResult route (
    @NonNull final MetamodelRoutingResult previousResult, @NonNull final String pathElement
  )
  {
    if (EmptyMetamodelRoute.is(previousResult)) {
      return routeRootPath(pathElement);
    }

    if (CollectionMetamodelRoute.is(previousResult)) {
      return routeCollectionPath((CollectionMetamodelRoute) previousResult, pathElement);
    }

    if (ModelMetamodelRoute.is(previousResult)) {
      return routeModelPath((ModelMetamodelRoute) previousResult, pathElement);
    }

    throw new Error("Unhandled result type " + previousResult.toString());
  }

  private @NonNull MetamodelRoutingResult routeModelPath (
    @NonNull final ModelMetamodelRoute previousResult, @NonNull final String pathElement
  )
  {
    if (HttpModelControllerHandlerFactory.getHandlerFor(previousResult.getModelController())
          .isSupportingGetOperation()) {
      return InvalidMetamodelRoute.getInstance();
    } else {
      return InvalidMetamodelRoute.getInstance();
    }
  }

  private @NonNull MetamodelRoutingResult routeCollectionPath (
    @NonNull final CollectionMetamodelRoute previousResult, @NonNull final String pathElement
  )
  {
    if (IS_LONG.test(pathElement)) {
      try {
        return new LongModelMetamodelRoute(pathElement,
          previousResult.getCollectionController().getModelController(),
          Long.parseLong(pathElement)
        );
      } catch (@NonNull final NumberFormatException exception) {
        return InvalidMetamodelRoute.getInstance();
      }
    } else if (IS_UUID4.test(pathElement)) {
      return new UUIDModelMetamodelRoute(pathElement,
        previousResult.getCollectionController().getModelController(),
        UUID.fromString(pathElement)
      );
    } else {
      return InvalidMetamodelRoute.getInstance();
    }
  }

  private @NonNull MetamodelRoutingResult routeRootPath (@NonNull final String pathElement) {
    @NonNull final Collection<@NonNull CollectionController> collectionControllers = _applicationContext.getBeansOfType(
      CollectionController.class).values();

    for (@NonNull final CollectionController controller : collectionControllers) {
      if (controller.getClass().isAnnotationPresent(RootCollectionController.class)) {
        @NonNull final RootCollectionController rootCollectionController = controller.getClass().getAnnotation(
          RootCollectionController.class);

        if (rootCollectionController.value().equals(pathElement.toLowerCase())) {
          return new CollectionMetamodelRoute(pathElement, controller);
        }
      }
    }

    return InvalidMetamodelRoute.getInstance();
  }
}
