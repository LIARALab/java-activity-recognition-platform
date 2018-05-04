package org.liara.api.data.entity;

import java.time.ZonedDateTime;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ApplicationEntitySnapshot extends BaseEntitySnapshot<ApplicationEntity>
{
  @NonNull
  private final Long          _identifier;

  @NonNull
  private final ZonedDateTime _creationDate;

  @NonNull
  private final ZonedDateTime _updateDate;

  @Nullable
  private final ZonedDateTime _deletionDate;
  
  public ApplicationEntitySnapshot (@NonNull final ApplicationEntity model) {
    super(model);
    
    _identifier = model.getIdentifier();
    _creationDate = model.getCreationDate();
    _updateDate = model.getUpdateDate();
    _deletionDate = model.getDeletionDate();
  }

  public ApplicationEntitySnapshot (@NonNull final ApplicationEntitySnapshot toCopy) {
    super(toCopy);
    
    _identifier = toCopy.getIdentifier();
    _creationDate = toCopy.getCreationDate();
    _updateDate = toCopy.getUpdateDate();
    _deletionDate = toCopy.getDeletionDate();
  }
  
  public Long getIdentifier () {
    return _identifier;
  }
  
  public ZonedDateTime getCreationDate () {
    return _creationDate;
  }
  
  public ZonedDateTime getUpdateDate () {
    return _updateDate;
  }
  
  public ZonedDateTime getDeletionDate () {
    return _deletionDate;
  }

  @Override
  public ApplicationEntitySnapshot clone () {
    return new ApplicationEntitySnapshot(this);
  }
}
