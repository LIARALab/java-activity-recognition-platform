package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.liara.api.utils.Beans;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for all application entities.
 * 
 * This base class support a standard application identifier field, and
 * declare usual entities lifecycle timestamps fields.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
@MappedSuperclass
public class ApplicationEntity
       implements Cloneable
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "identifier", nullable = false, updatable = false, unique = true)
  @Nullable
  private Long          _identifier;

  @Column(name = "created_at", nullable = false, updatable = false, unique = false, precision = 6)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @Nullable
  private ZonedDateTime _creationDate;

  @Column(name = "updated_at", nullable = false, updatable = true, unique = false, precision = 6)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @UpdateTimestamp
  @Nullable
  private ZonedDateTime _updateDate;

  @Column(name = "deleted_at", nullable = true, updatable = true, unique = false, precision = 6)
  @Nullable
  private ZonedDateTime _deletionDate;
  
  /**
   * Instanciate a new empty application entity.
   */
  public ApplicationEntity () {
    _identifier = null;
    _creationDate = null;
    _updateDate = null;
    _deletionDate = null;
  }

  /**
   * Instanciate a clone of another application entity.
   *
   * @param toCopy Application entity to clone.
   */
  public ApplicationEntity (@NonNull final ApplicationEntity toCopy) {
    _identifier = toCopy.getIdentifier();
    _creationDate = toCopy.getCreationDate();
    _updateDate = toCopy.getUpdateDate();
    _deletionDate = toCopy.getDeletionDate();
  }
  
  /**
   * Entity identifier. 
   * 
   * Unique for all entities into a given collection, an unsaved entity will
   * return a null identifier.
   * 
   * @return The entity identifier, unique for each entities into a given collection.
   */
  public Long getIdentifier () {
    return _identifier == null ? null : _identifier.longValue();
  }
  
  public void setIdentifier (@NonNull final Long identifier) {
    _identifier = identifier;
  }

  /**
   * Return the date of the first insertion of this entity into the application database.
   * 
   * This getter will return null if the entity was not already inserted into the database. The creation 
   * date is automatically initialized at the first entity insertion into the database. 
   * 
   * This date use by default the time zone of the server that run the application.
   *
   * @return The date of the first insertion of this entity into the application database.
   */
  public ZonedDateTime getCreationDate () {
    return _creationDate;
  }
  
  public void setCreationDate (@NonNull final ZonedDateTime date) {
    _creationDate = date;
  }

  /**
   * Return the date of deletion of this entity from the application database.
   * 
   * This date allow the application to support soft-delete functionality : in order to not erase
   * entities from the database, the application ignore entries with a non-null deletion date by default.
   * 
   * The deletion date is automatically initialized after a call of delete method, and automatically removed 
   * after a call of the restore method. Do not forget to update the current entity and all entities impacted 
   * by a deletion or a restoration operation.
   * 
   * This date use by default the time zone of the server that run the application.
   *
   * @return The date of deletion of this entity from the application database.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getDeletionDate () {
    return _deletionDate;
  }
  
  public void setDeletionDate (@Nullable final ZonedDateTime date) {
    _deletionDate = date;
  }

  /**
   * Return the last update date of this entity into the database.
   * 
   * Return the date of the last mutation of this entity into the database. This date is
   * a read-only field automatically setted before any insertion or update operations.
   * 
   * If the entity was not already inserted into the application database, this getter will
   * return null.
   * 
   * This date use by default the time zone of the server that run the application.
   * 
   * @return The date of the last mutation of this entity into the database.
   */
  public ZonedDateTime getUpdateDate () {
    return _updateDate;
  }
  
  public void setUpdateDate (@Nullable final ZonedDateTime updateDate) {
    _updateDate = updateDate;
  }

  @JsonIgnore
  public ApplicationEntityReference<? extends ApplicationEntity> getReference () {
    return ApplicationEntityReference.of(this);
  }

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode () {
    if (getIdentifier() == null) return super.hashCode();
    else return Objects.hash(getClass(), getIdentifier());
  }

  /**
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals (@Nullable final Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (!getClass().isAssignableFrom(object.getClass())) return false;
    ApplicationEntity other = (ApplicationEntity) object;
    
    if (getIdentifier() == null) return false;
    else return Objects.equals(getIdentifier(), other.getIdentifier());
  }
  
  public boolean lookLike (@NonNull final Map<String, ?> values) {
    return Beans.lookLike(this, values);
  }
  
  public ApplicationEntitySnapshot snapshot () {
    return new ApplicationEntitySnapshot(this);
  }

  @Override
  protected ApplicationEntity clone () {
    return new ApplicationEntity(this);
  }
}
