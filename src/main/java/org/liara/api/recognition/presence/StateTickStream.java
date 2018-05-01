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
package org.liara.api.recognition.presence;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class StateTickStream implements TickStream
{
  @NonNull
  private final Iterator<BooleanState> _states;
  
  @Nullable
  private Tick _next = null;

  public StateTickStream (@NonNull final Iterator<BooleanState> states) {
    _states = states;
  }

  public StateTickStream (@NonNull final Iterable<BooleanState> states) {
    _states = states.iterator();
  }

  public StateTickStream (@NonNull final BooleanState... states) {
    _states = Arrays.asList(states).iterator();
  }
  
  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  @Override
  public Tick next () {
    update();
    if (_next == null) throw new NoSuchElementException();
    
    final Tick result = _next;
    _next = null;
    return result;
  }
  
  private void update () {
    while (_next == null && _states.hasNext()) {
      final BooleanState state = _states.next();
      
      if (state.getValue() == true) {
        _next = new Tick(state.getSensor(), state.getEmittionDate());
      }
    }
  }
}
