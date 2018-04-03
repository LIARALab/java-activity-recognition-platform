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

import java.util.Iterator;

import org.springframework.lang.NonNull;

/**
 * A cursor over a collection of element.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public final class Cursor
{
  @NonNull public static final Cursor ALL = new Cursor (0, Integer.MAX_VALUE);
  @NonNull public static final Cursor DEFAULT = new Cursor (0, 10);
  @NonNull public static final Cursor EMPTY = new Cursor (0, 0);
  
  private final int _offset;
  private final int _limit;
  
  public Cursor () {
    this._offset = 0;
    this._limit = 10;
  }
  
  public Cursor (final int offset) {
    this._offset = offset;
    this._limit = 10;
  }
  
  public Cursor (final int offset, final int limit) {
    this._offset = offset;
    this._limit = limit;
  }
  
  public Cursor (@NonNull final Cursor cursor) {
    this._offset = cursor.getOffset();
    this._limit = cursor.getLimit();
  }
  
  /**
   * @return The index of the first element to display.
   */
  public int getOffset () {
    return _offset;
  }
  
  /**
   * Return a new cursor based on this one with a new offset value.
   * 
   * @param offset The new offset value.
   * @return An updated cursor instance with the given offset.
   */
  public Cursor setOffset (final int offset) {
    return new Cursor (offset, _limit);
  }
  
  /**
   * @return The maximum number of element to display.
   */
  public int getLimit () {
    return _limit;
  }
  
  /**
   * Return a new cursor based on this one with a new limit value.
   * 
   * @param limit The new limit value.
   * @return An updated cursor instance with the given limit.
   */
  public Cursor setLimit (final int limit) {
    return new Cursor(_offset, limit);
  }
  
  /**
   * Return a new cursor based on this one without any limits.
   * 
   * @return An updated cursor instance with the given limit.
   */
  public Cursor unlimit () {
    return new Cursor(_offset, Integer.MAX_VALUE);
  }
  
  /**
   * @return True if the given cursor limit its results.
   */
  public boolean hasLimit () {
    return _limit != Integer.MAX_VALUE;
  }
}
