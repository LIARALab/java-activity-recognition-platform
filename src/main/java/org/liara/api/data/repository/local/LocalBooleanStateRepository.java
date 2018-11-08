package org.liara.api.data.repository.local;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.repository.ValueStateRepository;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalBooleanStateRepository
  extends LocalStateRepository<BooleanState>
  implements ValueStateRepository
{
  public static LocalBooleanStateRepository from (@NonNull final ApplicationEntityManager parent) {
    final LocalBooleanStateRepository result = new LocalBooleanStateRepository();
    result.setParent(parent);
    return result;
  }
  
  public LocalBooleanStateRepository() {
    super(BooleanState.class);
  }

  @Override
  public List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
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
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
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
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    boolean value
  ) {
    final List<BooleanState> result = findAll(inputSensors); 
    return result.stream().filter(x -> x.getValue() == value).collect(Collectors.toList());
  }
}
