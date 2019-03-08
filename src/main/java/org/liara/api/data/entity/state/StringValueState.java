package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_string")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class StringValueState
  extends ValueState<java.lang.String>
{
  public StringValueState () {
    super(java.lang.String.class);
  }

  public StringValueState (final @NonNull StringValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable String getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable String value) {
    super.setValue(value);
  }
}
