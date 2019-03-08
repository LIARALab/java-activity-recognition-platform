package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_boolean")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class BooleanValueState
  extends ValueState<java.lang.Boolean>
{
  public BooleanValueState () {
    super(java.lang.Boolean.class);
  }

  public BooleanValueState (final @NonNull BooleanValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable Boolean getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable Boolean value) {
    super.setValue(value);
  }
}
