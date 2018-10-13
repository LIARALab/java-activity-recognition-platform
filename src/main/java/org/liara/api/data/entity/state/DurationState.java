package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;

@Entity
@Table(name = "states_duration")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class DurationState
  extends State
{
  @Nullable
  private ZonedDateTime _start;

  @Nullable
  private ZonedDateTime _end;

  public DurationState () {
    super();
    _start = null;
    _end = null;
  }

  public DurationState (@NonNull final DurationState toCopy) {
    super(toCopy);
    _start = toCopy.getStart();
    _end = toCopy.getEnd();
  }

  @Transient
  public @Nullable Duration getDuration () {
    return (_start == null || _end == null) ? null : Duration.between(_start, _end);
  }

  @Transient
  public @Nullable Long getMilliseconds () {
    @Nullable final Duration duration = getDuration();
    return (duration == null) ? null : duration.getSeconds() * 1_000L + duration.getNano() / 1_000_000L;
  }

  @Column(name = "start", nullable = false, updatable = true, unique = false)
  public @Nullable ZonedDateTime getStart () {
    return _start;
  }

  public void setStart (@Nullable final ZonedDateTime start) {
    _start = start;
  }

  @Column(name = "end", nullable = true, updatable = true, unique = false)
  public @Nullable ZonedDateTime getEnd () {
    return _end;
  }

  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }

  public boolean contains (@NonNull final ZonedDateTime date) {
    if (_start == null && _end == null) {
      return true;
    } else if (_start != null && _end == null) {
      return date.compareTo(getStart()) >= 0;
    } else if (_start == null && _end != null) {
      return date.compareTo(getEnd()) < 0;
    } else {
      return date.compareTo(getStart()) >= 0 && date.compareTo(getEnd()) < 0;
    }
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends DurationState> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull State clone () {
    return new DurationState(this);
  }
}
