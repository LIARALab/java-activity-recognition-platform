package org.liara.api.data.entity.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@JsonPropertyOrder({"type", "identifier"})
@JsonDeserialize(using = ApplicationEntityReferenceDeserializer.class)
@JsonSerialize(using = ApplicationEntityReferenceSerializer.class)
public class ApplicationEntityReference<ReferencedEntity extends ApplicationEntity>
{
  @NonNull
  private final Class<? extends ReferencedEntity> _type;

  @Nullable
  private final Long _identifier;

  public static <ReferencedEntity extends ApplicationEntity> @NonNull Collection<@NonNull Long> identifiers (
    @NonNull final Iterable<@NonNull ApplicationEntityReference<ReferencedEntity>> inputs
  )
  {
    @NonNull final Set<@NonNull Long> result = new HashSet<>();

    for (@NonNull final ApplicationEntityReference<ReferencedEntity> reference : inputs) {
      if (!reference.isNull()) {
        result.add(reference.getIdentifier());
      }
    }

    return result;
  }

  public static <ReferencedEntity extends ApplicationEntity> @NonNull Collection<@NonNull ApplicationEntityReference<ReferencedEntity>> of (
    @NonNull final Class<? extends ReferencedEntity> type, @NonNull final Iterable<@NonNull Long> identifiers
  )
  {
    @NonNull final Set<@NonNull ApplicationEntityReference<ReferencedEntity>> result = new HashSet<>();

    for (@NonNull final Long identifier : identifiers) {
      result.add(ApplicationEntityReference.of(type, identifier));
    }

    return result;
  }

  public static <ReferencedEntity extends ApplicationEntity> @NonNull ApplicationEntityReference<ReferencedEntity> of (
    @NonNull final ReferencedEntity entity
  )
  { return new ApplicationEntityReference<>(entity); }

  public static <ReferencedEntity extends ApplicationEntity> @NonNull ApplicationEntityReference<ReferencedEntity> of (
    @NonNull final Class<? extends ReferencedEntity> type, @Nullable final Long identifier
  )
  { return new ApplicationEntityReference<>(type, identifier); }

  public static <ReferencedEntity extends ApplicationEntity> @NonNull ApplicationEntityReference<ReferencedEntity> empty (
    @NonNull final Class<? extends ReferencedEntity> type
  )
  { return new ApplicationEntityReference<>(type, null); }

  public ApplicationEntityReference (
    @NonNull final Class<? extends ReferencedEntity> type
  )
  {
    _type = type;
    _identifier = null;
  }

  public ApplicationEntityReference (
    @NonNull final Class<? extends ReferencedEntity> type, @Nullable final Long identifier
  )
  {
    _type = type;
    _identifier = identifier;
  }

  @SuppressWarnings("unchecked") // entity is an instance of Entity, so entity.getClass() is Class<? extends Entity)
  public ApplicationEntityReference (
    @NonNull final ReferencedEntity entity
  )
  {
    _type = (Class<? extends ReferencedEntity>) entity.getClass();
    _identifier = entity.getIdentifier();
  }

  public @NonNull Class<? extends ReferencedEntity> getType () {
    return _type;
  }

  public @Nullable Long getIdentifier () {
    return _identifier;
  }

  public boolean isNull () {
    return _identifier == null;
  }

  @JsonIgnore
  public ReferencedEntity resolve (@NonNull final EntityManager entityManager) {
    return (_identifier == null) ? null : entityManager.find(_type, _identifier);
  }

  @Override
  public int hashCode () {
    return Objects.hash(ApplicationEntity.getBaseTypeOf(_type), _identifier);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (this == other) return true;
    if (other == null) return false;

    if (other instanceof ApplicationEntityReference) {
      @NonNull final ApplicationEntityReference<?> otherReference = (ApplicationEntityReference<?>) other;

      return Objects.equals(_identifier, otherReference.getIdentifier()) &&
             Objects.equals(ApplicationEntity.getBaseTypeOf(_type),
                            ApplicationEntity.getBaseTypeOf(otherReference.getType())
             );
    }

    return false;
  }

  @Override
  public String toString () {
    if (isNull()) {
      return String.join("", ApplicationEntityReference.class.getName(), "[", _type.getName(), "@null]");
    }

    return String.join("",
                       ApplicationEntityReference.class.getName(),
                       "[",
                       _type.getName(),
                       "@",
                       _identifier.toString(),
                       "]"
    );
  }

  public boolean is (@NonNull final Class<?> type) {
    return type.isAssignableFrom(_type);
  }

  public <Type extends ApplicationEntity> ApplicationEntityReference<Type> as (
    @NonNull final Class<Type> type
  )
  {
    return ApplicationEntityReference.of(type, _identifier);
  }
}
