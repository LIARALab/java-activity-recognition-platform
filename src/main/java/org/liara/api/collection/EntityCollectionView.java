/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.collection;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A view over an entity collection.
 *
 * A view is only a part of an entity collection that can be returned to a
 * client or used for computation.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * @param <Entity> Type of entity in the view.
 */
public class EntityCollectionView<Entity>
{
  /**
   * The view definition.
   */
  private Cursor                   _cursor;

  /**
   * The parent collection of this view.
   */
  @NonNull
  private EntityCollection<Entity, ?> _parent;

  /**
   * The content of this view.
   */
  @Nullable
  private List<Entity>       _content;

  /**
   * Create a new view over all a collection.
   *
   * @param parent The parent collection of this view.
   */
  public EntityCollectionView(@NonNull final EntityCollection<Entity, ?> parent) {
    this.setParentCollection(parent);
    _cursor = Cursor.ALL;
    _content = null;
  }

  /**
   * Create a new view over a collection.
   *
   * @param parent The parent collection of this view.
   * @param cursor The view definition.
   */
  public EntityCollectionView(@NonNull final EntityCollection<Entity, ?> parent, @NonNull final Cursor cursor) {
    this.setParentCollection(parent);
    _cursor = cursor;
    _content = null;
  }

  /**
   * Return the parent collection of this view.
   *
   * @return The parent collection of this view.
   */
  public EntityCollection<Entity, ?> getParentCollection () {
    return _parent;
  }

  /**
   * Change the parent collection of this view.
   *
   * @param parent The new parent collection of this view.
   */
  public void setParentCollection (@NonNull final EntityCollection<Entity, ?> parent) {
    _parent = parent;
    _content = null;
  }

  /**
   * Return the definition of this view.
   *
   * @return The definition of this view.
   */
  public Cursor getCursor () {
    return _cursor;
  }

  /**
   * Change the definition of this view.
   *
   * @param cursor The new definition of this view.
   */
  public void setCursor (@NonNull final Cursor cursor) {
    _cursor = cursor;
    _content = null;
  }

  /**
   * Return the real index of the first element of this view.
   *
   * @return The index of the first element of this view.
   */
  public long getFirst () {
    if (this.getContent().size() == 0) {
      return _parent.getSize();
    } else {
      return _cursor.getOffset();
    }
  }

  /**
   * Return the index of the last element of this view.
   *
   * @return The index of the last element of this view.
   */
  public long getLast () {
    return this.getFirst() + this.getSize();
  }

  /**
   * Return the number of elements in this view.
   *
   * @return The number of elements in this view.
   */
  public long getSize () {
    return this.getContent().size();
  }

  /**
   * Return the content of this view.
   *
   * @return The content of this view.
   */
  public List<Entity> getContent () {
    if (_content == null) this.update();
    return _content;
  }

  /**
   * Update this view content.
   */
  public void update () {
    final TypedQuery<Entity> query = this.getParentCollection().createQuery();

    if (_cursor.hasLimit()) {
      query.setMaxResults(_cursor.getLimit());
    }

    _content = query.setFirstResult(_cursor.getOffset()).getResultList();
  }
}
