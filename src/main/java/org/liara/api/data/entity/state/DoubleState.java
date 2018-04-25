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
package org.liara.api.data.entity.state;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import javax.persistence.Column;

@Entity
@Table(name = "states_double")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class DoubleState extends State
{
  @Column(name = "value", updatable = true, nullable = false, unique = false)
  private double _value;

  public double getValue () {
    return _value;
  }

  public void setValue (final double value) {
    _value = value;
  }

  @Override
  public String toString () {
    StringBuilder builder = new StringBuilder();
    builder.append("DoubleState [");
    if (getIdentifier() != null) {
      builder.append("getIdentifier()=");
      builder.append(getIdentifier());
      builder.append(", ");
    }
    builder.append("getSensorIdentifier()=");
    builder.append(getSensorIdentifier());
    builder.append(", ");
    if (getEmittionDate() != null) {
      builder.append("getEmittionDate()=");
      builder.append(getEmittionDate());
      builder.append(", ");
    }
    if (getCreationDate() != null) {
      builder.append("getCreationDate()=");
      builder.append(getCreationDate());
      builder.append(", ");
    }
    if (getDeletionDate() != null) {
      builder.append("getDeletionDate()=");
      builder.append(getDeletionDate());
      builder.append(", ");
    }
    if (getUpdateDate() != null) {
      builder.append("getUpdateDate()=");
      builder.append(getUpdateDate());
      builder.append(", ");
    }
    builder.append("_value=");
    builder.append(_value);
    builder.append("]");
    return builder.toString();
  }
}
