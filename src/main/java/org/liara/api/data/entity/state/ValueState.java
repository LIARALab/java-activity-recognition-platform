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

  @Override
  public @NonNull ValueState<Value> clone () {
    return new ValueState<>(this);
  }

  @MappedSuperclass
  public static class Numeric<NumericValue extends Number>
    extends ValueState<NumericValue>
  {
    public Numeric () {
      super();
    }

    public Numeric (final ValueState.@NonNull Numeric<NumericValue> toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Numeric<NumericValue> clone () {
      return new ValueState.Numeric<>(this);
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

    @Override
    public ValueState.@NonNull Byte clone () {
      return new ValueState.Byte(this);
    }
  }

  @Entity
  @Table(name = "states_short")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Short
    extends ValueState<java.lang.Short>
  {
    public Short () {
      super();
    }

    public Short (final ValueState.@NonNull Short toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Short clone () {
      return new ValueState.Short(this);
    }
  }

  @Entity
  @Table(name = "states_integer")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Integer
    extends ValueState<java.lang.Integer>
  {
    public Integer () {
      super();
    }

    public Integer (final ValueState.@NonNull Integer toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Integer clone () {
      return new ValueState.Integer(this);
    }
  }

  @Entity
  @Table(name = "states_long")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Long
    extends ValueState<java.lang.Long>
  {
    public Long () {
      super();
    }

    public Long (final ValueState.@NonNull Long toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Long clone () {
      return new ValueState.Long(this);
    }
  }

  @Entity
  @Table(name = "states_float")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Float
    extends ValueState<java.lang.Float>
  {
    public Float () {
      super();
    }

    public Float (final ValueState.@NonNull Float toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Float clone () {
      return new ValueState.Float(this);
    }
  }

  @Entity
  @Table(name = "states_double")
  @PrimaryKeyJoinColumn(name = "state_identifier")
  public static class Double
    extends ValueState<java.lang.Double>
  {
    public Double () {
      super();
    }

    public Double (final ValueState.@NonNull Double toCopy) {
      super(toCopy);
    }

    @Override
    public ValueState.@NonNull Double clone () {
      return new ValueState.Double(this);
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

    @Override
    public ValueState.@NonNull Boolean clone () {
      return new ValueState.Boolean(this);
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

    @Override
    public ValueState.@NonNull String clone () {
      return new ValueState.String(this);
    }
  }
}
