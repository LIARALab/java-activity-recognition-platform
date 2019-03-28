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
  extends NumericValueState<Long>
{
  public LongValueState () {
    super(Long.class);
  }

  public LongValueState (@NonNull final LongValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public @Nullable Long getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (@Nullable final Long value) {
    super.setValue(value);
  }
}
