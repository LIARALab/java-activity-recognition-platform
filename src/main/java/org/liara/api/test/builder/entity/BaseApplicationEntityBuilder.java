package org.liara.api.test.builder.entity;

import java.time.ZonedDateTime;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.test.builder.Builder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class BaseApplicationEntityBuilder<
                        Self extends BaseApplicationEntityBuilder<Self, Entity>, 
                        Entity extends ApplicationEntity
                      >
                implements Builder<Self, Entity>
{
  @Nullable
  private Long _identifier;
  
  @Nullable
  private ZonedDateTime _creationDate;
  
  @Nullable
  private ZonedDateTime _updateDate;
  
  @Nullable
  private ZonedDateTime _deletionDate;
  
  public Self withIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
    return self();
  }
  
  public Self withDeletionDate (@Nullable final ZonedDateTime deletionDate) {
    _deletionDate = deletionDate;
    return self();
  }
  
  public Self withUpdateDate (@Nullable final ZonedDateTime updateDate) {
    _updateDate = updateDate;
    return self();
  }
  
  public Self withCreationDate (@Nullable final ZonedDateTime creationDate) {
    _creationDate = creationDate;
    return self();
  }
  
  protected void apply (@NonNull final ApplicationEntity entity) {
    entity.setIdentifier(_identifier);
    entity.setCreationDate(_creationDate);
    entity.setUpdateDate(_updateDate);
    entity.setDeletionDate(_deletionDate);
  }
  
  public abstract Entity build ();

  public Entity buildFor (@NonNull final LocalEntityManager entityManager) {
    final Entity result = build();
    entityManager.add(result);
    return result;
  }
  
  public abstract Self self ();
}
