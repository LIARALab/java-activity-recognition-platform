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
package org.liara.recognition.presence;

import java.util.NoSuchElementException;

import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LiaraPresenceStream implements PresenceStream
{
  @NonNull
  private final EventStream _events;
  
  @Nullable
  private Event _lastEvent = null;
  
  @Nullable
  private ActivationState _next = null;
  
  public LiaraPresenceStream (@NonNull final EventStream events) {
    _events = events;
  }

  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  @Override
  public ActivationState next () {
    update();
    if (_next == null) throw new NoSuchElementException();
    
    final ActivationState result = _next;
    _next = null;
    return result;
  }

  private void update () {
    while (_next == null && _events.hasNext()) {
      final Event next = _events.next();
      
      if (_lastEvent == null) {
        _lastEvent = next;
      } else {
        if (_lastEvent.getSensor().equals(next.getSensor())) {
          _lastEvent = _lastEvent.setEnd(next.getEnd());
        } else {
          _next = toPresence(_lastEvent);
          _lastEvent = next;
        }
      }
    }
    
    if (_next == null && !_events.hasNext() && _lastEvent != null) {
      _next = toOccuringPresence(_lastEvent);
      _lastEvent = null;
    }
  }

  private ActivationState toOccuringPresence (Event event) {
    final ActivationState result = new ActivationState();
    result.setStart(event.getStart());
    result.setEnd(null);
    result.setNode(event.getSensor().getNodes().get(0));
    result.setEmittionDate(event.getStart());
    return result;  
  }

  private ActivationState toPresence (@NonNull final Event event) {
    final ActivationState result = new ActivationState();
    result.setStart(event.getStart());
    result.setEnd(event.getEnd());
    result.setNode(event.getSensor().getNodes().get(0));
    result.setEmittionDate(event.getEnd());
    return result;
  }
}
