package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@JsonPropertyOrder({ "type", "identifier" })
@JsonDeserialize(using = ApplicationEntityReferenceDeserializer.class)
@JsonSerialize(using = ApplicationEntityReferenceSerializer.class)
public class ApplicationEntityReference<ReferencedEntity extends ApplicationEntity>
{
  @Nullable
  private static EntityManager SHARED_ENTITY_MANAGER;
   
  @NonNull
  private final Class<? extends ReferencedEntity> _type;
  
  @NonNull
  private final Long _identifier;
  
  public static <ReferencedEntity extends ApplicationEntity> Collection<Long> identifiers (
    @NonNull final Iterable<ApplicationEntityReference<ReferencedEntity>> inputs
  ) {
    return Streams.stream(inputs).map(ApplicationEntityReference::getIdentifier).collect(Collectors.toList());
  }
  
  public static <ReferencedEntity extends ApplicationEntity> Collection<ApplicationEntityReference<ReferencedEntity>> of (
    @NonNull final Class<? extends ReferencedEntity> type,
    @NonNull final Iterable<Long> identifiers
  ) {
    return Streams.stream(identifiers).map(
      (final Long identifier) -> {
        final ApplicationEntityReference<ReferencedEntity> reference = ApplicationEntityReference.of(type, identifier);
        return reference;
      }
    ).collect(Collectors.toList());
  }
  
  public static <ReferencedEntity extends ApplicationEntity> ApplicationEntityReference<ReferencedEntity> of (
    @NonNull final ReferencedEntity entity
  ) { return new ApplicationEntityReference<>(entity); }
  
  public static <ReferencedEntity extends ApplicationEntity> ApplicationEntityReference<ReferencedEntity> of (
    @NonNull final Class<? extends ReferencedEntity> type,
    @Nullable final Long identifier
  ) { return new ApplicationEntityReference<>(type, identifier); }
  
  public static <ReferencedEntity extends ApplicationEntity> ApplicationEntityReference<ReferencedEntity> empty (
    @NonNull final Class<? extends ReferencedEntity> type
  ) { return new ApplicationEntityReference<>(type, null); }
  
  public ApplicationEntityReference (
    @NonNull final Class<? extends ReferencedEntity> type,
    @Nullable final Long identifier
  ) {
     _type = type;
     _identifier = identifier;
  }
  
  @SuppressWarnings("unchecked") // entity is an instance of Entity, so entity.getClass() is Class<? extends Entity)
  public ApplicationEntityReference(
    @NonNull final ReferencedEntity entity
  ) {
    _type = (Class<? extends ReferencedEntity>) entity.getClass();
    _identifier = entity.getIdentifier();
  }
  
  public Class<? extends ReferencedEntity> getType () {
    return _type;
  }
  
  public Long getIdentifier () {
    return _identifier;
  }
  
  public boolean isNull () {
    return _identifier == null;
  }
  
  @SuppressWarnings("unchecked")
  protected Class<? extends ApplicationEntity> getTypeOf (@NonNull final Class<? extends ApplicationEntity> subType) {
    if (!subType.isAnnotationPresent(Entity.class)) return null;
    
    Class<? extends ApplicationEntity> result = subType;
    
    while (result.getSuperclass().isAnnotationPresent(Entity.class)) {
      result = (Class<? extends ApplicationEntity>) result.getSuperclass();
    }
    
    return result;
  }
  
  @JsonIgnore
  public ReferencedEntity resolve () {
    if (_identifier == null) return null;
    
    return ApplicationEntityReference.SHARED_ENTITY_MANAGER.find(_type, _identifier);
  }
  
  public ReferencedEntity resolve (@NonNull final EntityManager entityManager) {
    if (_identifier == null) return null;
    
    return entityManager.find(_type, _identifier);
  }
  
  @Autowired
  protected synchronized void registerDefaultEntityManager (@NonNull final EntityManager entityManager) {
    ApplicationEntityReference.SHARED_ENTITY_MANAGER = entityManager;
  }

  @Override
  public int hashCode () {
    return Objects.hash(getTypeOf(_type), _identifier);
  }

  @Override
  public boolean equals (@Nullable final Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (!(object instanceof ApplicationEntityReference)) return false;
    
    ApplicationEntityReference<?> other = (ApplicationEntityReference<?>) object;
   
    return Objects.equals(_identifier, other.getIdentifier()) &&
           Objects.equals(getTypeOf(_type), getTypeOf(other.getType()));
  }
  
  @Override
  public String toString () {
    if (isNull()) {
      return String.join(
        "", 
        ApplicationEntityReference.class.getName(),
        "[", _type.getName(), "@null]"
      );
    }
    
    return String.join(
      "", 
      ApplicationEntityReference.class.getName(),
      "[", _type.getName(), "@", _identifier.toString(), "]"
    );
  }

  public boolean is (@NonNull final Class<?> type) {
    return type == _type && type.isAssignableFrom(_type);
  }
  
  public <Type extends ApplicationEntity> ApplicationEntityReference<Type> as (
    @NonNull final Class<Type> type
  ) {
    return ApplicationEntityReference.of(type, _identifier);
  }
}
