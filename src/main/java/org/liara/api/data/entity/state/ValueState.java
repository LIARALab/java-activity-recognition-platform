package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.utils.ObjectIdentifiers;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.NoSuchElementException;
import java.util.Objects;

@MappedSuperclass
public class ValueState<Value>
  extends State
{
  @Nullable
  private Value _value;

  @NonNull
  private Class<Value> _type;

  public ValueState (@NonNull final Class<Value> type) {
    super();
    _value = null;
    _type = type;
  }

  public ValueState (@NonNull final Class<Value> type, @NonNull final ValueState<?> toCopy) {
    super(toCopy);
    _value = null;
    _type = type;
  }

  public ValueState (@NonNull final ValueState<Value> toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
    _type = toCopy.getType();
  }

  @Transient
  public @Nullable Value getValue () {
    return _value;
  }

  @Transient
  public @NonNull Value requireValue ()
  throws NoSuchElementException {
    return Objects.requireNonNull(getValue());
  }

  @Transient
  public void setValue (@Nullable final Value value) {
    _value = value;
  }

  @Transient
  @JsonIgnore
  public @NonNull Class<Value> getType () {
    return _type;
  }

  @Override
  public @NonNull String toString () {
    return ObjectIdentifiers.getIdentifier(this) + " " +
           "{" +
           " universalUniqueIdentifier: " + getUniversalUniqueIdentifier() + ", " +
           " identifier: " + getIdentifier() + ", " +
           " updateDate: " + getUpdateDate() + ", " +
           " creationDate: " + getCreationDate() + ", " +
           " deletionDate: " + getDeletionDate() + ", " +
           " sensorIdentifier: " + getSensorIdentifier() + ", " +
           " emissionDate: " + getEmissionDate() + ", " +
           " value: " + _value + ", " +
           " type: " + _type.getName() +
           " } ";
  }
}
