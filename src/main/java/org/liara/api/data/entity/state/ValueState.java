package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.*;

@MappedSuperclass
public class ValueState<Value>
  extends State
{
  @Nullable
  private Value _value;

  public ValueState () {
    super();
    _value = null;
  }

  public ValueState (@NonNull final ValueState<Value> toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  @Column(name = "value", nullable = false)
  public @Nullable Value getValue () {
    return _value;
  }

  public void setValue (@Nullable final Value value) {
    _value = value;
  }

  @MappedSuperclass
  public static abstract class Numeric<NumericValue extends Number>
    extends ValueState<NumericValue>
  {
    public Numeric () {
      super();
    }

    public Numeric (final ValueState.@NonNull Numeric<NumericValue> toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_byte")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Byte
    extends ValueState.Numeric<java.lang.Byte>
  {
    public Byte () {
      super();
    }

    public Byte (final ValueState.@NonNull Byte toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_short")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Short
    extends ValueState.Numeric<java.lang.Short>
  {
    public Short () {
      super();
    }

    public Short (final ValueState.@NonNull Short toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_integer")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Integer
    extends ValueState.Numeric<java.lang.Integer>
  {
    public Integer () {
      super();
    }

    public Integer (final ValueState.@NonNull Integer toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_long")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Long
    extends ValueState.Numeric<java.lang.Long>
  {
    public Long () {
      super();
    }

    public Long (final ValueState.@NonNull Long toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_float")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Float
    extends ValueState.Numeric<java.lang.Float>
  {
    public Float () {
      super();
    }

    public Float (final ValueState.@NonNull Float toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_double")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Double
    extends ValueState.Numeric<java.lang.Double>
  {
    public Double () {
      super();
    }

    public Double (final ValueState.@NonNull Double toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_boolean")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Boolean
    extends ValueState<java.lang.Boolean>
  {
    public Boolean () {
      super();
    }

    public Boolean (final ValueState.@NonNull Boolean toCopy) {
      super(toCopy);
    }
  }

  @Entity
  @Table(name = "states_string")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class String
    extends ValueState<java.lang.String>
  {
    public String () {
      super();
    }

    public String (final ValueState.@NonNull String toCopy) {
      super(toCopy);
    }
  }
}
