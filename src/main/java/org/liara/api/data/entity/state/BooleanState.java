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
@Table(name = "states_boolean")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class BooleanState extends State
{
  @Nullable
  private Boolean _value;

  public BooleanState () {
    super();
    _value = null;
  }

  public BooleanState (@NonNull final BooleanState toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  @Column(name = "value", nullable = false, unique = false, updatable = true)
  public @Nullable Boolean getValue () {
    return _value;
  }

  public void setValue (@Nullable final Boolean value) {
    _value = value;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends BooleanState> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull BooleanState clone () {
    return new BooleanState(this);
  }
}
