package org.liara.api.collection.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.liara.api.collection.Cursor;
import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityCollectionView;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.collection.ordering.Ordering;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.parser.cursor.APIRequestFreeCursorParser;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.ordering.ComposedAPIRequestOrderingParser;
import org.liara.api.request.validator.APIRequestFreeCursorValidator;
import org.liara.api.request.validator.APIRequestValidator;
import org.liara.api.request.validator.error.APIRequestError;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.lang.NonNull;

public interface CollectionRequestConfiguration<Entity>
{
  public static <Entity> CollectionRequestConfiguration<Entity> fromClass(
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> clazz
  ) {
    try {
      return (CollectionRequestConfiguration<Entity>) clazz.newInstance();
    } catch (final Exception e) {
      throw new Error(String.join("", 
        "Unnable to instanciate the collection configuration ",
        String.valueOf(clazz),
        " does the class has a public constructor without parameters ?"
      ));
    }
  }
  
  public static CollectionRequestConfiguration<?> fromRawClass(
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> clazz
  ) {
    try {
      return (CollectionRequestConfiguration<?>) clazz.newInstance();
    } catch (final Exception e) {
      throw new Error(String.join("", 
        "Unnable to instanciate the collection configuration ",
        String.valueOf(clazz),
        " does the class has a public constructor without parameters ?"
      ));
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <Entity, Identifier> CollectionRequestConfiguration<Entity> getDefault (
    @NonNull final Class<?> clazz
  ) {
    if (clazz.isAnnotationPresent(DefaultCollectionRequestConfiguration.class)) {
      final DefaultCollectionRequestConfiguration defaultCollection = clazz.getAnnotation(DefaultCollectionRequestConfiguration.class);
      
      try {
        return (CollectionRequestConfiguration<Entity>) defaultCollection.value().newInstance();
      } catch (final Exception e) {
        throw new Error(String.join("", 
          "Unnable to instanciate the collection configuration ",
          String.valueOf(defaultCollection.value()),
          " does the class has a public constructor without parameters ?"
        ));
      }
    } else {
      return new EmptyCollectionRequestConfiguration<>();
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <Entity> Class<? extends CollectionRequestConfiguration<Entity>> getDefaultClass (
    @NonNull final Class<?> clazz
  ) {
    if (clazz.isAnnotationPresent(DefaultCollectionRequestConfiguration.class)) {
      final DefaultCollectionRequestConfiguration defaultCollection = clazz.getAnnotation(DefaultCollectionRequestConfiguration.class);
       return (Class<? extends CollectionRequestConfiguration<Entity>>) defaultCollection.value();
    } else {
      return (Class<? extends CollectionRequestConfiguration<Entity>>) new EmptyCollectionRequestConfiguration<Entity>().getClass();
    }
  }
  
  public static <Entity, Identifier> CollectionRequestConfiguration<Entity> getDefault (
    @NonNull final EntityCollection<Entity, Identifier> collection
  ) {
    return getDefault(collection.getClass());
  }
  
  public static void validate (
    @NonNull final Collection<APIRequestValidator> validators, 
    @NonNull final APIRequest request
  ) throws InvalidAPIRequestException {
    final List<APIRequestError> errors = new ArrayList<>();
    
    for (final APIRequestValidator validator : validators) {
      errors.addAll(validator.validate(request));
    }
    
    if (errors.size() > 0) {
      throw new InvalidAPIRequestException(request, errors);
    }
  }
  
  public default <Identifier> EntityCollectionView<Entity, Identifier> applyRequest (
    @NonNull final APIRequest request,
    @NonNull final EntityCollection<Entity, Identifier> collection
  ) throws InvalidAPIRequestException {
    validate(request);
    
    final EntityFilter<Entity> filter = parseFilter(request);
    final Cursor cursor = parseCursor(request);
    final Ordering<Entity> ordering = parseOrdering(request);
    
    return collection.filter(filter).order(ordering).getView(cursor);
  }
  
  public default <Identifier> EntityCollection<Entity, Identifier> filterCollection (
    @NonNull final APIRequest request,
    @NonNull final EntityCollection<Entity, Identifier> collection
  ) throws InvalidAPIRequestException {
    validateFiltersAndSorts(request);
    
    final EntityFilter<Entity> filter = parseFilter(request);
    
    return collection.filter(filter);
  }
  
  public default <Identifier> EntityCollection<Entity, Identifier> orderCollection (
    @NonNull final APIRequest request,
    @NonNull final EntityCollection<Entity, Identifier> collection
  ) throws InvalidAPIRequestException {
    validateFiltersAndSorts(request);
    
    final Ordering<Entity> ordering = parseOrdering(request);
    
    return collection.order(ordering);
  }
  
  public default <Identifier> EntityCollection<Entity, Identifier> filterAndOrderCollection (
    @NonNull final APIRequest request,
    @NonNull final EntityCollection<Entity, Identifier> collection
  ) throws InvalidAPIRequestException {
    validateFiltersAndSorts(request);

    final EntityFilter<Entity> filter = parseFilter(request);
    final Ordering<Entity> ordering = parseOrdering(request);
    
    return collection.filter(filter).order(ordering);
  }
  
  public default EntityFilter<Entity> parseFilter (@NonNull final APIRequest request) {
    return createFilterParser().parse(request);
  }
  
  public default Cursor parseCursor (@NonNull final APIRequest request) {
    return createCursorParser().parse(request);
  }
  
  public default Ordering<Entity> parseOrdering (@NonNull final APIRequest request) {
    return createOrderingParser().parse(request);
  }
  
  public default void validate (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    CollectionRequestConfiguration.validate(createValidators(), request);
  }
  
  public default void validateFilters (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    CollectionRequestConfiguration.validate(createFilterValidators(), request);
  }
  
  public default void validateSorts (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    CollectionRequestConfiguration.validate(createSortValidators(), request);
  }
  
  public default void validateFiltersAndSorts (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    final List<APIRequestValidator> validators = new ArrayList<>();
    validators.addAll(createFilterValidators());
    validators.addAll(createSortValidators());
    
    CollectionRequestConfiguration.validate(validators, request);
  }
  
  public APIRequestParser<EntityFilter<Entity>> createFilterParser ();
  
  public default APIRequestParser<Cursor> createCursorParser () {
    return new APIRequestFreeCursorParser();
  }
  
  public default APIRequestParser<Ordering<Entity>> createOrderingParser () {
    return new ComposedAPIRequestOrderingParser<>(createOrderingProcessors());
  }
  
  public List<APIRequestOrderingProcessor<Entity>> createOrderingProcessors ();

  public default Collection<APIRequestValidator> createCursorValidators () {
    return Arrays.asList(new APIRequestFreeCursorValidator ());
  }
  
  public default Collection<APIRequestValidator> createSortValidators () {
    return Collections.emptyList();
  }
  
  public default Collection<APIRequestValidator> createFilterValidators () {
    return Collections.emptyList();
  }
  
  public default Collection<APIRequestValidator> createValidators () {
    final List<APIRequestValidator> validators = new ArrayList<>();
    
    validators.addAll(createCursorValidators());
    validators.addAll(createSortValidators());
    validators.addAll(createFilterValidators());
    
    return validators;
  }
}
