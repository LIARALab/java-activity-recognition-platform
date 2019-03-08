package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "states_byte")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class ByteValueState
  extends NumericValueState<java.lang.Byte>
{
  public ByteValueState () {
    super(java.lang.Byte.class);
  }

  public ByteValueState (final @NonNull ByteValueState toCopy) {
    super(toCopy);
  }

  @Column(name = "value", nullable = false)
  @Override
  public java.lang.@Nullable Byte getValue () {
    return super.getValue();
  }

  @Override
  public void setValue (final java.lang.@Nullable Byte value) {
    super.setValue(value);
  }
}
