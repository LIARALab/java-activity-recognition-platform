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
package org.liara.api.collection.view;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

/**
 * A view over an entity collection.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * @param <Entity> Type of entity stored into the view.
 */
public interface EntityCollectionView<Entity>
{
  /**
   * Return the stored entity type.
   * 
   * @return The stored entity type.
   */
  public Class<Entity> getEntityType ();
  
  /**
   * Return the content of this view.
   *
   * @return The content of this view.
   */
  public List<Entity> get ();
  
  /**
   * Return an element of this view.
   *
   * @param index Index of the element of this view to fetch.
   *
   * @return The requested element of this view.
   * 
   * @throws IndexOutOfBoundsException when the index is less than 0 or greather or equal to the size of this view.
   */
  public Entity get (final int index) throws IndexOutOfBoundsException;
 
  /**
   * Return the manager of all entities of this view.
   * 
   * @return The manager of all entities of this view.
   */
  public EntityManager getManager ();
  
  /**
   * Create and return a query that select the content of this view.
   * 
   * @return A query that select the content of this view.
   */
  public CriteriaQuery<Entity> createQuery ();
  
  /**
   * Return the size of this view.
   * 
   * @return The size of this view.
   */
  public long getSize ();
}
