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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.*;

@Entity
@Table(name = "states_double")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class DoubleState
  extends NumericState
{
  @Nullable
  private Double _value;

  public DoubleState () {
    super();
    _value = null;
  }

  public DoubleState (@NonNull final DoubleState toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  @Column(name = "value", updatable = true, nullable = false, unique = false)
  public @Nullable Double getValue () {
    return _value;
  }

  public void setValue (@Nullable final Double value) {
    _value = value;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends DoubleState> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull DoubleState clone () {
    return new DoubleState(this);
  }
}
