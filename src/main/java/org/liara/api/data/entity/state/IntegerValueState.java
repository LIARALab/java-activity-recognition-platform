package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_integer")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class IntegerValueState
  extends NumericValueState<java.lang.Integer>
{
  public IntegerValueState () {
    super(java.lang.Integer.class);
  }

  public IntegerValueState (final @NonNull IntegerValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable Integer getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable Integer value) {
    super.setValue(value);
  }
}
