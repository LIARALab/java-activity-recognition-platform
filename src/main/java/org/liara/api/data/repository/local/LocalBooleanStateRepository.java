package org.liara.api.data.repository.local;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.repository.BooleanStateRepository;
import org.springframework.lang.NonNull;

public class LocalBooleanStateRepository 
       extends LocalTimeSeriesRepository<BooleanState>
       implements BooleanStateRepository
{
  public static LocalBooleanStateRepository from (@NonNull final LocalEntityManager parent) {
    final LocalBooleanStateRepository result = new LocalBooleanStateRepository();
    result.setParent(parent);
    return result;
  }
  
  public LocalBooleanStateRepository() {
    super(BooleanState.class);
  }

  @Override
  public List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    final List<BooleanState> result = new ArrayList<>(count);
    final List<BooleanState> previous = findAllPrevious(date, inputSensors);
    
    for (int index = 0; index < previous.size() && result.size() < count; ++index) {
      if (previous.get(index).getValue() == value) {
        result.add(previous.get(index));
      }
    }
    
    return result;
  }

  @Override
  public List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    final List<BooleanState> result = new ArrayList<>(count);
    final List<BooleanState> nexts = findAllNext(date, inputSensors);
    
    for (int index = 0; index < nexts.size() && result.size() < count; ++index) {
      if (nexts.get(index).getValue() == value) {
        result.add(nexts.get(index));
      }
    }
    
    return result;
  }

  @Override
  public List<BooleanState> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, 
    boolean value
  ) {
    final List<BooleanState> result = findAll(inputSensors); 
    return result.stream().filter(x -> x.getValue() == value).collect(Collectors.toList());
  }
}
