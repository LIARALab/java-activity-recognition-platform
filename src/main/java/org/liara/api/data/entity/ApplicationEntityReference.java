package org.liara.api.data.entity;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@JsonPropertyOrder({ "type", "identifier" })
public class ApplicationEntityReference<Entity extends ApplicationEntity>
{
  @Nullable
  private static EntityManager SHARED_ENTITY_MANAGER;
   
  @NonNull
  private final Class<? extends Entity> _type;
  
  @NonNull
  private final Long _identifier;
  
  public static <Entity extends ApplicationEntity> ApplicationEntityReference<Entity> of (
    @NonNull final Entity entity
  ) { return new ApplicationEntityReference<>(entity); }
  
  public static <Entity extends ApplicationEntity> ApplicationEntityReference<Entity> of (
    @NonNull final Class<? extends Entity> type,
    @Nullable final Long identifier
  ) { return new ApplicationEntityReference<>(type, identifier); }
  
  public static <Entity extends ApplicationEntity> ApplicationEntityReference<Entity> empty (
    @NonNull final Class<? extends Entity> type
  ) { return new ApplicationEntityReference<>(type, null); }
  
  public ApplicationEntityReference (
    @NonNull final Class<? extends Entity> type,
    @Nullable final Long identifier
  ) {
     _type = type;
     _identifier = identifier;
  }
  
  @SuppressWarnings("unchecked") // entity is an instance of Entity, so entity.getClass() is Class<? extends Entity)
  public ApplicationEntityReference(
    @NonNull final Entity entity
  ) {
    _type = (Class<? extends Entity>) entity.getClass();
    _identifier = entity.getIdentifier();
  }
  
  public Class<? extends Entity> getType () {
    return _type;
  }
  
  public Long getIdentifier () {
    return _identifier;
  }
  
  public boolean isNull () {
    return _identifier == null;
  }
  
  @JsonIgnore
  public Entity resolve () {
    if (_identifier == null) return null;
    
    return ApplicationEntityReference.SHARED_ENTITY_MANAGER.find(_type, _identifier);
  }
  
  public Entity resolve (@NonNull final EntityManager entityManager) {
    if (_identifier == null) return null;
    
    return entityManager.find(_type, _identifier);
  }
  
  @Autowired
  protected synchronized void registerDefaultEntityManager (@NonNull final EntityManager entityManager) {
    ApplicationEntityReference.SHARED_ENTITY_MANAGER = entityManager;
  }
}
