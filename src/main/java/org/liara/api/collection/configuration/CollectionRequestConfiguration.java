package org.liara.api.collection.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.transformation.cursor.Cursor;
import org.liara.api.collection.transformation.cursor.CursorTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.Transformation;
import org.liara.api.collection.view.cursor.CursorView;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.parser.cursor.APIRequestFreeCursorParser;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.liara.api.request.parser.operator.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.operator.ordering.ComposedAPIRequestOrderingParser;
import org.liara.api.request.parser.transformation.grouping.APIRequestGroupingProcessor;
import org.liara.api.request.parser.transformation.grouping.ComposedAPIRequestGroupingParser;
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
  
  /**
   * Return the default configuration declared for a given collection type.
   * 
   * @param clazz A collection type.
   * 
   * @return The default configuration declared for the given collection type.
   */
  @SuppressWarnings("unchecked")
  public static <Entity> CollectionRequestConfiguration<Entity> getDefaultConfigurationOf (
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
  
  public static <Entity> CollectionRequestConfiguration<Entity> getDefaultConfigurationOf (
    @NonNull final EntityCollection<Entity> collection
  ) {
    return getDefaultConfigurationOf(collection.getClass());
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
  
  public default CursorTransformation getCursor (
    @NonNull final APIRequest request
  ) throws InvalidAPIRequestException {
    return CursorTransformation.from(createCursorParser().parse(request));
  }
  
  public default EntityCollectionOperator<Entity> getOperator (
    @NonNull final APIRequest request
  ) {
    final EntityCollectionOperator<Entity> filter = createFilterParser().parse(request);
    final EntityCollectionOperator<Entity> ordering = createOrderingParser().parse(request);
    
    return filter.apply(ordering);
  }
  

  public default EntityCollectionGroupTransformation<Entity> getGrouping (
    @NonNull final APIRequest request
  ) {
    return new ComposedAPIRequestGroupingParser<>(
      createGroupingProcessors()
    ).parse(request);
  }
  
  public default void validate (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    final List<APIRequestValidator> validators = new ArrayList<>();
    validators.addAll(createFilteringValidators());
    validators.addAll(createOrderingValidators());
    validators.addAll(createCursorValidators());
    
    CollectionRequestConfiguration.validate(validators, request);
  }
  
  public APIRequestEntityCollectionOperatorParser<Entity> createFilterParser ();
  
  public default APIRequestParser<Cursor> createCursorParser () {
    return new APIRequestFreeCursorParser();
  }
  
  public default APIRequestEntityCollectionOperatorParser<Entity> createOrderingParser () {
    return new ComposedAPIRequestOrderingParser<>(createOrderingProcessors());
  }
  
  public List<APIRequestOrderingProcessor<Entity>> createOrderingProcessors ();
  
  public List<APIRequestGroupingProcessor<Entity>> createGroupingProcessors ();

  public default Collection<APIRequestValidator> createCursorValidators () {
    return Arrays.asList(new APIRequestFreeCursorValidator ());
  }
  
  public default Collection<APIRequestValidator> createOrderingValidators () {
    return Collections.emptyList();
  }
  
  public default Collection<APIRequestValidator> createFilteringValidators () {
    return Collections.emptyList();
  }
}
