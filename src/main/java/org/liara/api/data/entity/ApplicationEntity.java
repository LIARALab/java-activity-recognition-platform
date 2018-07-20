/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.data.entity;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.liara.api.utils.Beans;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;

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

  @Column(name = "created_at", nullable = false, updatable = false, unique = false)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @Nullable
  private ZonedDateTime _creationDate;

  @Column(name = "updated_at", nullable = false, updatable = true, unique = false)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @UpdateTimestamp
  @Nullable
  private ZonedDateTime _updateDate;

  @Column(name = "deleted_at", nullable = true, updatable = true, unique = false)
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
   * Return the creation date of this entity into the application database.
   * 
   * This getter will return null if the entity was not already inserted into the database. The creation 
   * date is a read-only field automatically initialized at the first entity insertion into the database. 
   * 
   * This date use by default the time zone of the server that run the application.
   * 
   * @return The creation date of this entity into the application database.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getCreationDate () {
    return _creationDate;
  }
  
  public void setCreationDate (@NonNull final ZonedDateTime date) {
    _creationDate = date;
  }

  /**
   * Return the deletion date of this entity from the application database.
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
   * @see #delete()
   * @see #restore()
   * 
   * @return The deletion date of this entity from the application database.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getDeletionDate () {
    return _deletionDate;
  }
  
  public void setDeletionDate (@Nullable final ZonedDateTime date) {
    delete(date);
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getUpdateDate () {
    return _updateDate;
  }
  
  public void setUpdateDate (@Nullable final ZonedDateTime updateDate) {
    _updateDate = updateDate;
  }

  /**
   * Mark this entity as deleted.
   * 
   * If this entity was not deleted yet, i.e does not have a deletion date, this method will
   * set the deletion date of this entity as the default current date by using the application's 
   * server date zone.
   * 
   * If the entity was already deleted this method will do nothing.
   * 
   * Do not forget to update the related entries into the database after a call of this method,
   * and to change entities impacted by the deletion of this entity. You can also override this
   * method in order to add more advanced business logic.
   * 
   * @see #getDeletionDate()
   * @see #restore()
   */
  public void delete () {
    if (_deletionDate == null) {
      delete(ZonedDateTime.now());
    }
  }
  
  public void delete (@NonNull final ZonedDateTime date) {
    _deletionDate = date;
  }

  /**
   * Unmark a deleted entity.
   * 
   * If this entity was deleted, i.e does have a deletion date, this method will
   * remove the deletion date of this entity.
   * 
   * If the entity is not deleted, this method will do nothing.
   * 
   * Do not forget to update the related entries into the database after a call of this method,
   * and to change entities impacted by the restoration of this entity. You can also override this
   * method in order to add more advanced business logic.
   * 
   * @see #getDeletionDate()
   * @see #delete()
   */
  public void restore () {
    if (_deletionDate != null) _deletionDate = null;
  }

  /**
   * A callback called before the first insertion of this entity into the database.
   * 
   * @see PrePersist
   */
  @PrePersist
  public void willBeCreated () {
    _creationDate = ZonedDateTime.now();
    _updateDate = _creationDate;
  }

  /**
   * A callback called before an update of this entity into the database.
   * 
   * @see PreUpdate
   */
  @PreUpdate
  public void willBeUpdated () {
    _updateDate = ZonedDateTime.now();
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
  
  public ApplicationEntityReference<? extends ApplicationEntity> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  protected ApplicationEntity clone () {
    return new ApplicationEntity(this);
  }
}
