package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_float")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class FloatValueState
  extends NumericValueState<Float>
{
  public FloatValueState () {
    super(Float.class);
  }

  public FloatValueState (final @NonNull FloatValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public @Nullable Float getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final @Nullable Float value) {
    super.setValue(value);
  }
}
