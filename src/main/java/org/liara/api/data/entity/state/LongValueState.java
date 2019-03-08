package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_long")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class LongValueState
  extends NumericValueState<java.lang.Long>
{
  public LongValueState () {
    super(java.lang.Long.class);
  }

  public LongValueState (final @NonNull LongValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable Long getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable Long value) {
    super.setValue(value);
  }
}
