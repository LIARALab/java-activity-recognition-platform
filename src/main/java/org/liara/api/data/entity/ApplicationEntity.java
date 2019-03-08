package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.rest.model.IdentifiableModel;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Base class for all application entities.
 *
 * This base class declare an identifier field and common entities lifecycle timestamps fields.
 *
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
@MappedSuperclass
public class ApplicationEntity
  implements IdentifiableModel
{
  public static @NonNull Operator tags (@NonNull final Class<?> type) {
    return TagRelation.tag().apply(ApplicationEntity.tagRelations(type));
  }

  public static @NonNull Operator tags (
    @NonNull final Class<?> type,
    @NonNull final ApplicationEntity entity
  ) {
    return Tag.relations().apply(ApplicationEntity.tagRelations(type, entity));
  }

  public static @NonNull Operator tagRelations (@NonNull final Class<?> type) {
    return Composition.of(
      Filter.expression(":this.entityIdentifier = :super.identifier"),
      Filter.expression(":this.entityType = :type")
        .setParameter("type", type.getName())
    );
  }

  public static @NonNull Operator tagRelations (
    @NonNull final Class<?> type,
    @NonNull final ApplicationEntity entity
  ) {
    return Composition.of(
      Filter.expression(":this.entityIdentifier = :identifier")
        .setParameter("identifier", entity.getIdentifier()),
      Filter.expression(":this.entityType = :type")
        .setParameter("type", type.getName())
    );
  }

  @Nullable
  private Long _identifier;

  @Nullable
  private String _uuid;

  @Nullable
  private ZonedDateTime _creationDate;

  @Nullable
  private ZonedDateTime _updateDate;

  @Nullable
  private ZonedDateTime _deletionDate;

  /**
   * Instantiate a new empty application entity.
   */
  public ApplicationEntity () {
    _identifier = null;
    _creationDate = null;
    _updateDate = null;
    _deletionDate = null;
    _uuid = null;
  }

  /**
   * Instantiate a clone of another application entity.
   *
   * @param toCopy Application entity to clone.
   */
  public ApplicationEntity (@NonNull final ApplicationEntity toCopy) {
    _identifier = toCopy.getIdentifier();
    _creationDate = toCopy.getCreationDate();
    _updateDate = toCopy.getUpdateDate();
    _deletionDate = toCopy.getDeletionDate();
    _uuid = toCopy.getUniversalUniqueIdentifier();
  }

  /**
   * Return the base type of this entity.
   *
   * The base type of this entity is the class that define this entity root table.
   *
   * If the given entity class does not have a base type, the ApplicationEntity class will be returned by this method.
   *
   * @param entityClass An entity type to evaluate.
   * @return The given entity type's base type.
   */
  public static @NonNull Class<? extends ApplicationEntity> getBaseTypeOf (
    @NonNull final Class<? extends ApplicationEntity> entityClass
  )
  {
    @NonNull Class<? extends ApplicationEntity>  clazz      = entityClass;

    while (clazz != ApplicationEntity.class) {
      if (clazz.isAnnotationPresent(Entity.class)) return clazz;
      clazz = clazz.getSuperclass().asSubclass(ApplicationEntity.class);
    }

    return ApplicationEntity.class;
  }

  /**
   * Entity identifier.
   * Unique for all entities into a given collection, an unsaved entity will return a null identifier.
   *
   * @return The entity identifier, unique for each entities into a given collection.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "identifier", nullable = false, updatable = false, unique = true)
  @Override
  public @Nullable Long getIdentifier () {
    return _identifier;
  }

  public void setIdentifier (@Nullable @NonNegative final Long identifier) {
    _identifier = identifier;
  }

  @Column(name = "uuid", nullable = false, unique = true, scale = 40)
  @JsonProperty("uuid")
  public @Nullable String getUniversalUniqueIdentifier () {
    return _uuid;
  }

  public void setUniversalUniqueIdentifier (@Nullable final String uuid) {
    _uuid = uuid;
  }

  /**
   * Return the date of the first insertion of this entity into the application database.
   *
   * This getter will return null if the entity was not already inserted into the database. The creation date is
   * automatically initialized at the first entity insertion into the database.
   * 
   * This date use by default the time zone of the server that run the application.
   *
   * @return The date of the first insertion of this entity into the application database.
   */
  @Column(
    name = "created_at",
    nullable = false,
    updatable = false,
    columnDefinition = "DATETIME(6)"
  )
  @ColumnDefault(value = "CURRENT_TIMESTAMP(6)")
  public @Nullable ZonedDateTime getCreationDate () {
    return _creationDate;
  }

  public void setCreationDate (@Nullable final ZonedDateTime date) {
    _creationDate = date;
  }

  /**
   * Return the date of deletion of this entity from the application database.
   *
   * This date allow the application to support soft-doDelete functionality : in order to not erase entities from the
   * database, the application ignore entries with a non-null deletion date by default.
   *
   * The deletion date is automatically initialized after a call of doDelete method, and automatically removed after a
   * call of the restore method. Do not forget to patch the current entity and all entities impacted by a deletion or a
   * restoration operation.
   * 
   * This date use by default the time zone of the server that run the application.
   *
   * @return The date of deletion of this entity from the application database.
   */
  @Column(
    name = "deleted_at",
    columnDefinition = "DATETIME(6)"
  )
  public @Nullable ZonedDateTime getDeletionDate () {
    return _deletionDate;
  }

  public void setDeletionDate (@Nullable final ZonedDateTime date) {
    _deletionDate = date;
  }

  /**
   * Return the last patch date of this entity into the database.
   *
   * Return the date of the last mutation of this entity into the database. This date is a read-only field automatically
   * set before any insertion or patch operations.
   *
   * If the entity was not already inserted into the application database, this getter will return null.
   * 
   * This date use by default the time zone of the server that run the application.
   *
   * @return The date of the last mutation of this entity into the database.
   */
  @Column(
    name = "updated_at",
    nullable = false,
    columnDefinition = "DATETIME(6)"
  )
  @ColumnDefault(value = "CURRENT_TIMESTAMP(6)")
  @UpdateTimestamp
  public @Nullable ZonedDateTime getUpdateDate () {
    return _updateDate;
  }

  public void setUpdateDate (@Nullable final ZonedDateTime updateDate) {
    _updateDate = updateDate;
  }

  /**
   * Return the base type of this entity.
   * <p>
   * The base type of this entity is the class that define this entity root table.
   * <p>
   * If this entity does not have a base type, the ApplicationEntity class will be returned by this method.
   *
   * @return This entity base type.
   */
  @JsonIgnore
  @Transient
  public @NonNull Class<? extends ApplicationEntity> getBaseClass () {
    return ApplicationEntity.getBaseTypeOf(getClass());
  }

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode () {
    if (getIdentifier() == null) return super.hashCode();
    else return Objects.hash(getBaseClass(), getIdentifier());
  }

  /**
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals (@Nullable final Object other) {
    if (this == other) return true;
    if (other == null) return false;

    if (other instanceof ApplicationEntity) {
      @NonNull final ApplicationEntity otherEntity = (ApplicationEntity) other;

      return Objects.equals(getBaseClass(), otherEntity.getBaseClass()) &&
             Objects.equals(_identifier, otherEntity.getIdentifier());
    }

    return false;
  }
}
