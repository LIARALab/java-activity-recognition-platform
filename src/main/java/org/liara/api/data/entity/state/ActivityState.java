package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.*;

@Entity
@Table(name = "states_activity")
@PrimaryKeyJoinColumn(name = "state_identifier")
@JsonPropertyOrder({
  "identifier", "emittionDate", "sensorIdentifier", "start", "end", "duration", "milliseconds"
})
public class ActivityState
  extends DurationState
{
  @Column(name = "tag", nullable = false, updatable = true, unique = false)
  @Nullable
  private String _tag;

  public ActivityState () {
    super();
    _tag = null;
  }

  public ActivityState (@NonNull final ActivityState toCopy) {
    super(toCopy);
    _tag = toCopy.getTag();
  }

  public @Nullable String getTag () {
    return _tag;
  }

  public void setTag (@Nullable final String tag) {
    _tag = tag;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends ActivityState> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull ActivityState clone () {
    return new ActivityState(this);
  }
}
