package org.liara.api.collection.route;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.collection.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class APIRouteParser
{
  @NonNull
  private static final Pattern LONG_PATTERN = Pattern.compile("^\\d+$");

  @NonNull
  private static final Pattern UUID_PATTERN = Pattern.compile(
    "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$",
    Pattern.CASE_INSENSITIVE
  );

  @NonNull
  private final CollectionControllerManager _collectionControllerManager;

  @NonNull
  private final CollectionControllerHandlerFactory _collectionControllerHandlerFactory;

  @Autowired
  public APIRouteParser (
    @NonNull final CollectionControllerManager collectionControllerManager,
    @NonNull final CollectionControllerHandlerFactory collectionControllerHandlerFactory
  ) {
    _collectionControllerManager = collectionControllerManager;
    _collectionControllerHandlerFactory = collectionControllerHandlerFactory;
  }

  public @NonNull APIRoute parse (
    @NonNull final String path
  ) throws InvalidRouteException {
    return parse(PathContainer.parsePath(path));
  }

  public @NonNull APIRoute parse (
    @NonNull final PathContainer path
  ) throws InvalidRouteException {
    @NonNull final List<@NonNull APIRouteParserToken> tokens = new ArrayList<>(path.elements().size());
    @NonNull final String fullPath = path.value();
    @NonNegative int position = 0;

    for (final PathContainer.@NonNull Element element : path.elements()) {
      if (element instanceof PathContainer.PathSegment) {
        tokens.add(new APIRouteParserToken(element.value(), position, fullPath));
        position += element.value().length();
      } else {
        position += 1;
      }
    }

    return this.parse(tokens);
  }

  public @NonNull APIRoute parse (
    @NonNull final List<@NonNull APIRouteParserToken> tokens
  ) throws InvalidRouteException {
    @NonNull APIRoute result = new APIRoute();

    for (@NonNull final APIRouteParserToken element : tokens) {
      result = resolve(result, element);
    }

    return result;
  }

  private @NonNull APIRoute resolve (
    @NonNull final APIRoute result,
    @NonNull final APIRouteParserToken token
  ) throws InvalidRouteException {
    if (result.size() == 0) {
      return resolveRoot(token);
    } else if (result.getLast() instanceof APIRouteCollectionControllerElement) {
      return resolveAfterCollection(result, (APIRouteCollectionControllerElement) result.getLast(), token);
    } else if (result.getLast() instanceof APIRouteModelIdentifierElement) {
      return resolveAfterModel(result, token);
    } else {
      throw new Error("Invalid APIRouteParser state.");
    }
  }

  private @NonNull APIRoute resolveAfterModel (
    @NonNull final APIRoute result,
    @NonNull final APIRouteParserToken token
  ) {
    return result;
  }

  private @NonNull APIRoute resolveAfterCollection (
    @NonNull final APIRoute result,
    @NonNull final APIRouteCollectionControllerElement collection,
    @NonNull final APIRouteParserToken token
  ) throws InvalidRouteException {
    @NonNull final CollectionController<?> controller = collection.getCollectionController();
    @NonNull final CollectionControllerHandler<?> handler =
      _collectionControllerHandlerFactory.getCollectionControllerHandler(controller);

    if (handler.isSupportingGetOperation()) {
      if (LONG_PATTERN.asPredicate().test(token.getToken())) {
        return result.concat(new APIRouteLocalModelIdentifierElement(Long.parseLong(token.getToken()), null));
      } else if (UUID_PATTERN.asPredicate().test(token.getToken())) {
        return result.concat(new APIRouteLocalModelIdentifierElement(Long.parseLong(token.getToken()), null));
      } else {

      }
    } else {
      throw new InvalidRouteException(
        "Invalid path element '" + token.getToken() + "' at index " + token.getPosition() + " of path '" +
        token.getFullPath() + "' because the collection '" + collection.getToken() +
        "' does not allow to access to its elements."
      );
    }

    return result;
  }

  private @NonNull APIRoute resolveRoot (
    @NonNull final APIRouteParserToken token
  ) throws InvalidRouteException {
    @NonNull final String collectionName = token.getToken().toLowerCase();

    if (_collectionControllerManager.contains(collectionName)) {
      @NonNull final CollectionController<?> controller = _collectionControllerManager.get(collectionName);

      if (controller instanceof RootCollectionController) {
        return new APIRoute(new APIRouteCollectionControllerElement(collectionName, controller));
      } else {
        throw new InvalidRouteException(
          "Invalid path element '" + token.getToken() + "' at index " + token.getPosition() + " of path '" +
          token.getFullPath() + "' because the collection registered with the name '" +
          collectionName + "' is not accessible from the root directory."
        );
      }
    } else {
      throw new InvalidRouteException(
        "Invalid path element '" + token.getToken() + "' at index " + token.getPosition() + " of path '" +
        token.getFullPath() + "' because no collection was registered with the name '" +
        collectionName + "'."
      );
    }
  }

  public @NonNull CollectionControllerManager getCollectionControllerManager () {
    return _collectionControllerManager;
  }
}
