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

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;

@Entity
@Table(name = "states_integer")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(IntegerStateCreationSchema.class)
@UseMutationSchema(IntegerStateMutationSchema.class)
public class IntegerState extends NumericState
{
  @Column(name = "value", nullable = false, updatable = true, unique = false)
  private int _value;
  
  public IntegerState () { 
    _value = 0;
  }

  public int getValue () {
    return _value;
  }

  public void setValue (final int value) {
    _value = value;
  }

  @Override
  @JsonIgnore
  public Integer getNumber () {
    return _value;
  }
  
  @Override
  public IntegerStateSnapshot snapshot () {
    return new IntegerStateSnapshot(this);
  }  
  
  @Override
  public ApplicationEntityReference<? extends IntegerState> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
