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
package org.liara.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 *         An helper interface that present a parameter of an API Request and
 *         all of its values.
 */
public interface APIRequestParameter extends Iterable<String>
{
  /**
   * Return the parent request of this parameter.
   * 
   * @return The parent request of this parameter.
   */
  @JsonIgnore
  public APIRequest getRequest ();

  /**
   * Return the name of this parameter.
   * 
   * @return The name of this parameter.
   */
  public String getName ();

  /**
   * Return the number of values registered for this parameter.
   * 
   * @return The number of values registered for this parameter.
   */
  @JsonIgnore
  public int getValueCount ();

  /**
   * Return a value of this parameter.
   * 
   * @throws java.lang.IndexOutOfBoundsException When the given index is not between 0 and the total number of values for the given parameter.
   * 
   * @param index Index of the value to return.
   * @return The value defined at the given index of this parameter.
   */
  public String getValue (final int index) throws IndexOutOfBoundsException;

  /**
   * Return all values of this parameter.
   * 
   * @return All values of this parameter.
   */
  public String[] getValues ();
}
