package org.liara.api.recognition.sensor.common.virtual.condition;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.repository.BooleanStateRepository;
import org.liara.api.data.repository.NumericStateRepository;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(LtSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
public class LtSensor
  extends ConditionSensor<NumericState>
{
  @Autowired
  public LtSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final NumericStateRepository data,
    @NonNull final BooleanStateRepository output
  )
  {
    super(schemaManager, data, output);
  }

  @Override
  protected boolean check (@NonNull final NumericState state) {
    return state.getNumber().doubleValue() < getConfiguration().as(LtSensorConfiguration.class).getFloor();
  }
}
