package org.liara.api.data.repository.database;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.NumericStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@Scope("prototype")
@Primary
public class DatabaseNumericStateRepository
  implements NumericStateRepository
{
  private DatabaseTimeSeriesRepository<State> _states;

  @Autowired
  public DatabaseNumericStateRepository (
    @NonNull final EntityManager entityManager
  )
  {
    _states = new DatabaseTimeSeriesRepository<>(entityManager, State.class);
  }

  private @NonNull
  List<NumericState> toNumericStateList (@NonNull final List<State> states) {
    final List<NumericState> result = new ArrayList<>();

    states.forEach(state -> {
      if (state instanceof NumericState) result.add((NumericState) state);
    });

    return result;
  }

  @Override
  public @NonNull
  List<NumericState> findAllAt (
    @NonNull final ZonedDateTime time, @NonNull final Collection<ApplicationEntityReference<Sensor>> sensors
  )
  {
    return toNumericStateList(_states.findAllAt(time, sensors));
  }

  @Override
  public Optional<NumericState> find (@NonNull final ApplicationEntityReference<NumericState> identifier) {
    return find(identifier.getIdentifier());
  }

  @Override
  public Optional<NumericState> find (@NonNull final Long identifier) {
    final Optional<State> result = _states.find(identifier);

    if (result.isPresent()) {
      return result.get() instanceof NumericState ? Optional.of((NumericState) result.get()) : Optional.empty();
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<NumericState> findAll () {
    final List<State>        partialResult = _states.findAll();
    final List<NumericState> result        = new ArrayList<>();

    partialResult.forEach(state -> {
      if (state instanceof NumericState) result.add((NumericState) state);
    });

    return result;
  }

  @Override
  public List<NumericState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<Sensor> sensor, int count
  )
  {
    return toNumericStateList(_states.findPrevious(date, sensor, count));
  }

  @Override
  public List<NumericState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<Sensor> sensor, final int count
  )
  {
    return toNumericStateList(_states.findNext(date, sensor, count));
  }

  @Override
  public List<NumericState> find (
    @NonNull final ApplicationEntityReference<Sensor> sensor, final int offset, final int count
  )
  {
    return toNumericStateList(_states.find(sensor, offset, count));
  }

  @Override
  public List<NumericState> findAll (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  )
  {
    return toNumericStateList(_states.findAll(sensor));
  }

  @Override
  public List<NumericState> findAll (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> sensors
  )
  {
    return toNumericStateList(_states.findAll(sensors));
  }

  @Override
  public List<NumericState> findWithCorrelation (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  )
  {
    return toNumericStateList(_states.findWithCorrelation(name, correlated, sensor));
  }

  @Override
  public List<NumericState> findWithCorrelations (
    @NonNull final Map<String, ApplicationEntityReference<? extends State>> correlations,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  )
  {
    return toNumericStateList(_states.findWithCorrelations(correlations, sensor));
  }

  @Override
  public List<NumericState> findWithAnyCorrelation (
    @NonNull final Collection<String> keys,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  )
  {
    return toNumericStateList(_states.findWithAnyCorrelation(keys, correlated, sensor));
  }

  @Override
  public List<NumericState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final int count
  )
  {
    return toNumericStateList(_states.findPrevious(date, inputSensors, count));
  }

  @Override
  public List<NumericState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final int count
  )
  {
    return toNumericStateList(_states.findNext(date, inputSensors, count));
  }

  @Override
  public List<NumericState> findAllNext (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors
  )
  {
    return toNumericStateList(_states.findAllNext(date, inputSensors));
  }

  @Override
  public List<NumericState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors
  )
  {
    return toNumericStateList(_states.findAllPrevious(date, inputSensors));
  }
}
