package org.liara.api.data.collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EntityCollections
{
  @NonNull
  public static ActivationStateCollection ACTIVATION_STATES;
  
  @NonNull
  public static BooleanStateCollection BOOLEAN_STATES;
  
  @NonNull
  public static DoubleStateCollection DOUBLE_STATES;

  @NonNull
  public static IntegerStateCollection INTEGER_STATES;
  
  @NonNull
  public static NodeCollection NODES;

  @NonNull
  public static SensorCollection SENSORS;
  
  @NonNull
  public static StateCollection STATES;
  
  @Autowired
  public void registerActivationStateCollection (
    @NonNull final ActivationStateCollection collection
  ) {
    ACTIVATION_STATES = collection;
  }

  @Autowired
  public void registerBooleanStateCollection (
    @NonNull final BooleanStateCollection collection
  ) {
    BOOLEAN_STATES = collection;
  }

  @Autowired
  public void registerIntegerStateCollection (
    @NonNull final IntegerStateCollection collection
  ) {
    INTEGER_STATES = collection;
  }

  @Autowired
  public void registerNodeCollection (
    @NonNull final NodeCollection collection
  ) {
    NODES = collection;
  }

  @Autowired
  public void registerSensorCollection (
    @NonNull final SensorCollection collection
  ) {
    SENSORS = collection;
  }

  @Autowired
  public void registerStateCollection (
    @NonNull final StateCollection collection
  ) {
    STATES = collection;
  }
}
