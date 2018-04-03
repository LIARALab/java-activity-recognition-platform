/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.domus.api.data.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Presence
{
  private final LocalDateTime _start;
  private final LocalDateTime _end;
  private final Node _room;
  
  public Presence (
    @NonNull final LocalDateTime start,
    @NonNull final LocalDateTime end,
    @NonNull final Node room
  ) {
    _start = start;
    _end = end;
    _room = room;
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getEnd () {
    return _end;
  }
  
  public Presence setEnd (@NonNull final LocalDateTime end) {
    return new Presence(_start, end, _room);
  }

  @JsonIgnore
  public Node getRoom () {
    return _room;
  }
  
  public String getRoomName () {
    return this._room.getName();
  }
  
  public Duration getDuration () {
    return Duration.between(this._start, this._end);
  }
  
  public Presence merge (@NonNull final Presence other) {
    if (this.getStart().compareTo(other.getStart()) <= 0) {
      return new Presence(this.getStart(), other.getEnd(), this.getRoom());
    } else {
      return new Presence(other.getStart(), this.getEnd(), this.getRoom());
    }
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getStart () {
    return _start;
  }

  public Presence setStart (@NonNull final LocalDateTime start) {
    return new Presence(start, _end, _room);
  }
}
