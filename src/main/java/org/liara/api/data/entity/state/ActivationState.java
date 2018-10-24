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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.utils.CloneMemory;

import javax.persistence.*;

@Entity
@Table(name = "states_presence")
@PrimaryKeyJoinColumn(name = "state_identifier")
@JsonPropertyOrder({
  "identifier", "emittionDate", "sensorIdentifier", "start", "end", "duration", "milliseconds",
  "nodeIdentifier"
})
public class ActivationState
  extends DurationState
{
  @Nullable
  private Node _node;
  
  public ActivationState () {
    super();
    _node = null;
  }

  public ActivationState (@NonNull final ActivationState toCopy, @NonNull final CloneMemory clones) {
    super(toCopy, clones);
    _node = (toCopy.getNode() == null) ? null : clones.clone(toCopy.getNode());
  }

  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = true)
  public @Nullable Node getNode () {
    return _node;
  }

  public void setNode (@Nullable final Node node) {
    _node = node;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends ActivationState> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull ActivationState clone ()
  {
    return clone(new CloneMemory());
  }

  @Override
  public @NonNull ActivationState clone (@NonNull final CloneMemory clones) {
    return new ActivationState(this, clones);
  }
}
