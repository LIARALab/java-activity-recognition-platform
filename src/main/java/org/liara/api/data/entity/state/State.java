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
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
public class State extends ApplicationEntity
{
  @Nullable
  private ZonedDateTime _emissionDate;

  @Nullable
  private ApplicationEntityReference<? extends Sensor> _sensorIdentifier;
  
  public State () {
    _emissionDate = null;
    _sensorIdentifier = null;
  }

  public State (@NonNull final State toCopy) {
    super(toCopy);
    _emissionDate = toCopy.getEmissionDate();
    _sensorIdentifier = toCopy.getSensorIdentifier();
  }

  @Column(name = "sensor_identifier", nullable = false)
  public @Nullable ApplicationEntityReference<? extends Sensor> getSensorIdentifier () {
    return _sensorIdentifier;
  }

  public void setSensorIdentifier (@Nullable final ApplicationEntityReference<? extends Sensor> sensorIdentifier) {
    _sensorIdentifier = sensorIdentifier;
  }

  @Column(name = "emitted_at", nullable = false, updatable = true, unique = false)
  public @Nullable ZonedDateTime getEmissionDate () {
    return _emissionDate;
  }

  public void setEmissionDate (@Nullable final ZonedDateTime emissionDate) {
    _emissionDate = emissionDate;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends State> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
