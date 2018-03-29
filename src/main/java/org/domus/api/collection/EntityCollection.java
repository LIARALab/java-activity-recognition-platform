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
package org.domus.api.collection;

import javax.persistence.TypedQuery;

import org.springframework.lang.NonNull;

import org.domus.api.collection.exception.EntityNotFoundException;

/**
 * A wrapper over a collection of a type of entity of the API.
 * 
 * Allows you to do common requests over a predefined type of entity and help
 * you to construct more advanced research.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 */
public interface EntityCollection<Entity>
{
  /**
   * Return the size of the collection.
   *
   * @return The size of the collection.
   */
  public int getSize ();

  /**
   * Return a view of this collection.
   *
   * @param cursor Cursor used in order to define the view.
   *
   * @return A view of this collection.
   */
  public EntityCollectionView<Entity> getView (@NonNull final Cursor cursor);

  /**
   * Return the content of this collection.
   *
   * @return The content of this collection.
   */
  public default Iterable<Entity> getContent () {
    return this.createQuery().getResultList();
  }

  /**
   * Try to find an entity of the collection.
   *
   * @param identifier Identifier of the entity to get.
   */
  public Entity findById (final int identifier);

  /**
   * Try to find an entity of the collection.
   *
   * @param identifier Identifier of the entity to get.
   */
  public Entity findByIdOrFail (final int identifier) throws EntityNotFoundException;

  /**
   * Return an entity of the collection.
   *
   * @param index Position of the entity to return in this collection.
   */
  public Entity get (final int index);

  /**
   * Return a query over this collection.
   *
   * @return A query over this collection.
   */
  public TypedQuery<Entity> createQuery ();

  /**
   * Return a criteria query over this collection.
   *
   * A criteria query can be muted.
   *
   * @return A criteria query over this collection.
   */
  public EntityCollectionQuery<Entity, Entity> createCollectionQuery ();

  /**
   * Return a criteria query over this collection with a custom return type.
   *
   * A criteria query can be muted.
   *
   * @param clazz The custom return type of the query.
   *
   * @return A criteria query over this collection with a custom return type.
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz);
}
