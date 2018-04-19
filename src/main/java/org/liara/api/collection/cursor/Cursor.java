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
package org.liara.api.collection.cursor;

import org.springframework.lang.NonNull;

/**
 * A cursor that select a range of entities in a collection.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public final class Cursor
{
  /**
   * A cursor that select an entire collection of entities.
   */
  @NonNull public static final Cursor ALL = new Cursor (0, Integer.MAX_VALUE);
  
  /**
   * The default application cursor. Skip 0 entities and display 10 entities from the given collection.
   */
  @NonNull public static final Cursor DEFAULT = new Cursor (0, 10);
  
  /**
   * An empty cursor. Skip 0 entities and do not display any entities from the given collection.
   */
  @NonNull public static final Cursor EMPTY = new Cursor (0, 0);
  
  private final int _offset;
  private final int _limit;
  
  /**
   * Create a new empty cursor. Skip 0 entities and do not display any entities from the given collection.
   */
  public Cursor () {
    _offset = 0;
    _limit = 0;
  }
  
  /**
   * Create a cursor that skip 0 entities and limit entities to display.
   * 
   * @param limit Maximum number of entities to display.
   */
  public Cursor (final int limit) {
    _offset = 0;
    _limit = limit;
  }
  
  /**
   * Create a cursor that skip a given amount of entities and also limit entities to display.
   * 
   * @param offset Amount of entities to skip.
   * @param limit Maximum number of entities to display.
   */
  public Cursor (final int offset, final int limit) {
    _offset = offset;
    _limit = limit;
  }
  
  /**
   * Create a copy of a given cursor.
   * 
   * @param cursor The cursor instance to copy.
   */
  public Cursor (@NonNull final Cursor cursor) {
    _offset = cursor.getOffset();
    _limit = cursor.getLimit();
  }
  
  /**
   * @return The amount of entities to skip.
   */
  public int getOffset () {
    return _offset;
  }
  
  /**
   * Return a new cursor based on this one with a new offset value.
   * 
   * @param offset The new amount of entities to skip.
   * @return An updated cursor instance with the given offset.
   */
  public Cursor setOffset (final int offset) {
    return new Cursor (offset, _limit);
  }
  
  /**
   * @return The maximum number of entities to display.
   */
  public int getLimit () {
    return _limit;
  }
  
  /**
   * Return a new cursor based on this one with a new limit value.
   * 
   * @param limit The new maximum number of entities to display.
   * @return An updated cursor instance with the given limit.
   */
  public Cursor setLimit (final int limit) {
    return new Cursor(_offset, limit);
  }
  
  /**
   * Return a new cursor based on this one without any limits.
   * 
   * @return An updated cursor instance that does not limit the number of entities to display.
   */
  public Cursor unlimit () {
    return new Cursor(_offset, Integer.MAX_VALUE);
  }
  
  /**
   * Return a new cursor based on this one without any skipped entities.
   * 
   * @return An updated cursor instance that does not skip any entities.
   */
  public Cursor unskip () {
    return new Cursor(0, _limit);
  }
  
  /**
   * @return True if the given cursor limit the number of entities to display.
   */
  public boolean hasLimit () {
    return _limit != Integer.MAX_VALUE;
  }
}
