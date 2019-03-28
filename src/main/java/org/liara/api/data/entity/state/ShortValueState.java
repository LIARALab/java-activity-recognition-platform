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
  extends NumericValueState<Short>
{
  public ShortValueState () {
    super(Short.class);
  }

  public ShortValueState (@NonNull final ShortValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public @Nullable Short getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (@Nullable final Short value) {
    super.setValue(value);
  }
}
