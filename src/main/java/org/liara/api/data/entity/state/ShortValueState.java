package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_short")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class ShortValueState
  extends NumericValueState<java.lang.Short>
{
  public ShortValueState () {
    super(java.lang.Short.class);
  }

  public ShortValueState (final @NonNull ShortValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable Short getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable Short value) {
    super.setValue(value);
  }
}
