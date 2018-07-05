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
import javax.persistence.EntityManager;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;

@Entity
@Table(name = "states_double")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(DoubleStateCreationSchema.class)
@UseMutationSchema(DoubleStateMutationSchema.class)
public class DoubleState extends NumericState
{
  @Column(name = "value", updatable = true, nullable = false, unique = false)
  private double _value;
  
  public DoubleState () { }
  
  public DoubleState (
    @NonNull final EntityManager manager,
    @NonNull final DoubleStateCreationSchema schema
  ) {
    super(manager, schema);
    _value = schema.getValue();
  }

  public double getValue () {
    return _value;
  }

  @Override
  @JsonIgnore
  public Double getNumber () {
    return _value;
  }

  public void setValue (final double value) {
    _value = value;
  }
  
  @Override
  public DoubleStateSnapshot snapshot () {
    return new DoubleStateSnapshot(this);
  }
}
