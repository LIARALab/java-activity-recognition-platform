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
package org.liara.api.filter.ast;

import org.springframework.lang.NonNull;

public enum CommonFilterNodeType implements FilterNodeType
{
  CONJUNCTION("conjunction"),
  DISJUNCTION("disjunction"),
  
  GREATHER_THAN("greather than"),
  GREATHER_THAN_OR_EQUAL_TO("greather than or equal to"),
  LESS_THAN("less than"),
  LESS_THAN_OR_EQUAL_TO("less than or equal to"),
  BETWEEN("between"),
  EQUAL_TO("equal to"),
  IN("in"),
  LIKE("like"),
  REGEXP("regexp"),
  
  NOT("not"),
  
  VALUE_INTEGER("value<Integer>"),
  VALUE_FLOAT("value<Float>"),
  VALUE_LONG("value<Long>"),
  VALUE_DOUBLE("value<Double>"),
  VALUE_STRING("value<String>"),
  VALUE_DATETIME("value<PartialLocalDateTime>"),
  VALUE_TIME("value<PartialLocalTime>");
  
  @NonNull
  private final String _name;
  
  private CommonFilterNodeType (@NonNull final String name) {
    _name = name;
  }

  @Override
  public String getName () {
    return _name;
  }
}
