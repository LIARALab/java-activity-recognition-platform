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
import java.util.Optional;

import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.view.EntityCollectionQueryBasedView;
import org.springframework.lang.NonNull;

/**
 * A collection of entity.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 */
public interface EntityCollection<Entity> 
       extends   EntityCollectionQueryBasedView<Entity, Entity, List<Entity>>
{     
  /**
   * Try to find an entity of this collection by using it's identifier.
   * 
   * @param identifier Identifier of the entity to find.
   * @return An optional value with the fetched entity if exists or empty otherwise.
   */
  public <Identifier> Optional<Entity> findByIdentifier (final Identifier identifier);
  
  /**
   * Try to find an entity of this collection by using it's identifier.
   * 
   * @param identifier Identifier of the entity to find.
   * @return An optional value with the fetched entity if exists or empty otherwise.
   * 
   * @throws EntityNotFoundException If the requested entity does not exists in this collection.
   */
  public <Identifier> Entity findByIdentifierOrFail (final Identifier identifier) throws EntityNotFoundException;

  /**
   * Return an operator to apply to a given query in order to select all entities of this collection.
   * 
   * @return An operator to apply to a given query in order to select all entities of this collection.
   */
  public EntityCollectionOperator<Entity> getOperator ();
  
  /**
   * Apply an operator to this collection and return a new updated instance of this collection.
   * 
   * @param operator Operator to apply to this collection.
   * 
   * @return A new updated instance of this collection.
   */
  public EntityCollection<Entity> apply (@NonNull final EntityCollectionOperator<Entity> operator);
}
