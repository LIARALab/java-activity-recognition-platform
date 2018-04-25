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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TickEventStream implements EventStream
{
  @NonNull
  private final TickStream _ticks;
  
  @Nullable
  private Event _next = null;
  
  @Nullable
  private Tick _lastTick = null;
  
  public TickEventStream (@NonNull final TickStream ticks) {
    _ticks = ticks;
  }
  
  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  private void update () {
    while (_next == null && _ticks.hasNext()) {
      final Tick next = _ticks.next();
      
      if (_lastTick == null) {
        _lastTick = next;
      } else if (!_lastTick.getSensor().equals(next.getSensor())) {
        _next = new Event(_lastTick.getSensor(), _lastTick.getDate(), next.getDate());
        _lastTick = next;
      }
    }
    
    if (_next == null && _ticks.hasNext() == false && _lastTick != null) {
      _next = new Event(_lastTick.getSensor(), _lastTick.getDate());
      _lastTick = null;
    }
  }

  @Override
  public Event next () {
    update();

    if (_next == null) throw new NoSuchElementException();
    
    final Event result = _next;
    _next = null;
    return result;
  }

}
