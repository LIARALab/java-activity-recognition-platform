package org.domus.api.collection;

import java.util.Collection;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A view over an entity collection.
 *
 * A view is only a part of an entity collection that can be returned to a
 * client or used for computation.
 */
public class EntityCollectionView<T>
{
  /**
   * The offset of this view. It is the index of the first item displayed.
   */
  private int                 _offset;

  /**
   * The maximum size allowed for this view. It is the maximum number of elements
   * to display into this view.
   */
  private int                 _maxSize;

  /**
   * The parent collection of this view.
   */
  @NonNull
  private EntityCollection<T> _parent;

  /**
   * The content of this view.
   */
  @Nullable
  private Collection<T>       _content;

  /**
   * Create a new view over all a collection.
   *
   * @param parent The parent collection of this view.
   */
  public EntityCollectionView(@NonNull final EntityCollection<T> parent) {
    this.setParentCollection(parent);
    this._offset = 0;
    this._maxSize = parent.getSize();
    this._content = null;
  }

  /**
   * Create a new view over a collection that has a maximum size.
   *
   * @param parent The parent collection of this view.
   * @param maxSize The maximum size of this view.
   */
  public EntityCollectionView(@NonNull final EntityCollection<T> parent, final int maxSize) {
    this.setParentCollection(parent);
    this._offset = 0;
    this._maxSize = maxSize;
    this._content = null;
  }

  /**
   * Create a new view over a collection with an offset and a maximum size.
   *
   * @param parent The parent collection of this view.
   * @param offset The offset of this view.
   * @param maxSize The size of this view.
   */
  public EntityCollectionView(@NonNull final EntityCollection<T> parent, final int offset, final int maxSize) {
    this.setParentCollection(parent);
    this._offset = offset;
    this._maxSize = maxSize;
    this._content = null;
  }

  /**
   * Return the parent collection of this view.
   *
   * @return The parent collection of this view.
   */
  public EntityCollection<T> getParentCollection () {
    return this._parent;
  }

  /**
   * Change the parent collection of this view.
   *
   * @param parent The new parent collection of this view.
   */
  public void setParentCollection (@NonNull final EntityCollection<T> parent) {
    this._parent = parent;
    this._content = null;
  }

  /**
   * Return the offset of this view.
   *
   * @return The offset of this view.
   */
  public int getOffset () {
    return this._offset;
  }

  /**
   * Change the offset of this view.
   *
   * @param offset The new offset of this view.
   */
  public void setOffset (final int offset) {
    this._offset = offset;
    this._content = null;
  }

  /**
   * Return the maximum size of this view.
   *
   * @return The maximum size of this view.
   */
  public int getMaxSize () {
    return this._maxSize;
  }

  /**
   * Change the maximum size of this view.
   *
   * @param maxSize The new maximum size of this view.
   */
  public void setMaxSize (final int maxSize) {
    this._maxSize = maxSize;
    this._content = null;
  }

  /**
   * Return the real index of the first element of this view.
   *
   * @return The index of the first element of this view.
   */
  public int getFirst () {
    if (this.getContent().size() == 0) {
      return this._parent.getSize();
    } else {
      return this._offset;
    }
  }

  /**
   * Return the index of the last element of this view.
   *
   * @return The index of the last element of this view.
   */
  public int getLast () {
    return this.getFirst() + this.getSize();
  }

  /**
   * Return the number of elements in this view.
   *
   * @return The number of elements in this view.
   */
  public int getSize () {
    return this.getContent().size();
  }

  /**
   * Return the content of this view.
   *
   * @return The content of this view.
   */
  public Collection<T> getContent () {
    if (this._content == null) this.update();
    return this._content;
  }

  /**
   * Update this view content.
   */
  public void update () {
    this._content = this.getParentCollection().createQuery().setMaxResults(this._maxSize).setFirstResult(this._offset)
      .getResultList();
  }
}
