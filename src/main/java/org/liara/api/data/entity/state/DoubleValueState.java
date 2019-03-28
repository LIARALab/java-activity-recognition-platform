package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_double")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class DoubleValueState
  extends NumericValueState<Double>
{
  public DoubleValueState () {
    super(Double.class);
  }

  public DoubleValueState (final @NonNull DoubleValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public @Nullable Double getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final @Nullable Double value) {
    super.setValue(value);
  }
}
