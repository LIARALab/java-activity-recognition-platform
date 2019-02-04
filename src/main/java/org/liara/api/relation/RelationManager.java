package org.liara.api.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RelationManager
{
  @NonNull
  private final Map<@NonNull Class<?>, @NonNull Map<@NonNull String, @NonNull Relation>> _relations;

  @NonNull
  private final Map<@NonNull Class<?>, @NonNull Map<@NonNull String, @NonNull Relation>> _readOnlyRelations;

  public RelationManager () {
    _relations = new HashMap<>();
    _readOnlyRelations = new HashMap<>();
  }

  public @NonNull Map<@NonNull String, @NonNull Relation> getRelationsOf (
    @NonNull final Class<?> clazz
  ) {
    if (!_relations.containsKey(clazz)) {
      computeRelationsOf(clazz);
    }

    return _readOnlyRelations.get(clazz);
  }

  public @NonNull Map<@NonNull String, @NonNull Relation> getRelationOf (
    @NonNull final ManagedType<?> type
  ) {
    return getRelationsOf(type.getJavaType());
  }

  private void computeRelationsOf (@NonNull final Class<?> clazz) {
    @NonNull final Map<@NonNull String, @NonNull Relation> relations = new HashMap<>();
    _readOnlyRelations.put(clazz, Collections.unmodifiableMap(relations));
    _relations.put(clazz, relations);

    for (@NonNull final Method method : clazz.getMethods()) {
      if (method.isAnnotationPresent(RelationFactory.class)) {
        relations.put(
          computeRelationNameFromMethod(method),
          computeRelationFromMethod(method)
        );
      }
    }
  }

  private @NonNull Relation computeRelationFromMethod (@NonNull final Method method) {
    try {
      @NonNull final RelationFactory factory          =
        method.getDeclaredAnnotation(RelationFactory.class);
      @NonNull final Class<?>        sourceClass      = method.getDeclaringClass();
      @NonNull final Class<?>        destinationClass = factory.value();
      @NonNull final Method          absoluteFactory  = method;
      @NonNull final Method relativeFactory = method.getDeclaringClass().getDeclaredMethod(
        method.getName(), sourceClass
      );

      @NonNull final FactoryBasedRelationBuilder<?, ?> builder = FactoryBasedRelationBuilder.create(
        sourceClass, destinationClass
      );

      builder.setAbsoluteFactory(absoluteFactory);
      builder.setRelativeFactory(relativeFactory);

      return new FactoryBasedRelation<>(builder);
    } catch (@NonNull final NoSuchMethodException exception) {
      throw new Error(
        "No relative factory discovered for the absolute factory " + method.toString() +
        " declared into class " + method.getDeclaringClass().getName()
      );
    }
  }

  private @NonNull String computeRelationNameFromMethod (@NonNull final Method method) {
    return Pattern.compile("[A-Z]").matcher(method.getName()).replaceAll(
      (@NonNull final MatchResult result) -> "-" + result.group().toLowerCase()
    );
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof RelationManager) {
      @NonNull final RelationManager otherRelationManager = (RelationManager) other;

      return Objects.equals(_relations, otherRelationManager._relations);
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_relations);
  }
}
