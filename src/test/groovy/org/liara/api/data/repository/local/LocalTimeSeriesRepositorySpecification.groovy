package org.liara.api.data.repository.local

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

import org.junit.After
import org.liara.api.data.entity.ApplicationEntityReference
import org.liara.api.data.entity.sensor.Sensor
import org.liara.api.data.entity.state.ActivityState
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.state.State
import org.liara.api.test.builder.IdentityBuilder
import org.liara.api.test.builder.sensor.SensorBuilder
import org.liara.api.test.builder.state.ActivityStateBuilder
import org.liara.api.test.builder.state.BooleanStateBuilder
import org.springframework.lang.NonNull
import spock.lang.Specification

public class LocalTimeSeriesRepositorySpecification
       extends Specification
{
  def List<BooleanState> generateUpDownStates (
    final int count, 
    @NonNull final ZonedDateTime start,
    @NonNull final Duration duration
  ) {
    final List<BooleanState> result = []
    final SensorBuilder builder = new SensorBuilder()
    
    for (int index = 0; index < count; ++index) {
      builder.with(
        BooleanStateBuilder.create({
          withValue index % 2 == 0
          withEmittionDate start.plus(duration.multipliedBy(index))
        })
      )
    }
    
    result.addAll(builder.build().getStates())
    Collections.sort(result, { a, b -> a.getEmittionDate().compareTo(b.getEmittionDate()) })
    
    return result
  }
  
  def List<ActivityState> generateActivities (
    @NonNull final List<BooleanState> flags,
    @NonNull final String tag
  ) {
    final List<ActivityState> result = new ArrayList<>();
    final SensorBuilder builder = new SensorBuilder()
        
    for (int index = 0; index < flags.size(); index += 2) {
      builder.with(
        ActivityStateBuilder.create {
          withTag tag
          withEmittionDate flags.get(index).getEmittionDate()
          withStart flags.get(index).getEmittionDate()
          withEnd flags.get(index + 1).getEmittionDate()
          withCorrelation "start", flags.get(index)
          withCorrelation "end", flags.get(index + 1)
        }
      )
    }
    
    result.addAll(builder.build().getStates())
    Collections.sort(result, { a, b -> a.getEmittionDate().compareTo(b.getEmittionDate()) })
    
    return result
  }
  
  def List<List<BooleanState>> generateUpDownSensors (
    final int sensors, 
    final int states
  ) {
    final List<List<BooleanState>> results = []
    
    for (int index = 0; index < sensors; ++index) {
      results.add(
        generateUpDownStates(
          states,
          ZonedDateTime.of(2012, 8, 12, 5, index, 00, 00, ZoneId.systemDefault()),
          Duration.ofMinutes(5)
        )
      )
    }
    
    return results
  }
  
  def List<List<ActivityState>> generateActivitySensors (
    final String[] tags 
  ) {
    final List<List<ActivityState>> results = []
    final List<BooleanState> flags = generateUpDownStates(
      20, 
      ZonedDateTime.of(2012, 8, 12, 5, 00, 00, 00, ZoneId.systemDefault()), 
      Duration.ofMinutes(5)
    )
    
    for (int index = 0; index < tags.length; ++index) {
      results.add(
        generateActivities(flags, tags[index])
      )
    }
    
    return results
  }
  
  def <StoredState extends State> void randomlyAdd (
    @NonNull final List<StoredState> states, 
    @NonNull final LocalTimeSeriesRepository<StoredState> repository
  ) {
    final List<StoredState> toAdd = new ArrayList<>(states)
    Collections.shuffle(toAdd)
    repository.addAll(toAdd)
  }
  
  def "it allows you to get states that was emitted before another state" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<BooleanState> repository = new LocalTimeSeriesRepository<BooleanState>(
        new LocalEntityManager(), BooleanState.class
      )
      final List<List<BooleanState>> sensorsStates = generateUpDownSensors(10, 30)
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get the n previous emittions of a given time series"
      for (int index = 1; index < 30; ++index) {
        for (final List<BooleanState> states : sensorsStates) {
          states.subList(0, index).equals(
            repository.findPrevious(states.get(index), index)
          ) == true
          
          states.get(index - 1).equals(
            repository.findPrevious(states.get(index))
          ) == true
        }
      }
  }
  
  def "it allows you to get states that was emitted after another state" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<BooleanState> repository = new LocalTimeSeriesRepository<BooleanState>(
        new LocalEntityManager(), BooleanState.class
      )
      final List<List<BooleanState>> sensorsStates = generateUpDownSensors(10, 30)
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get the n next emittions of a given time series"
      for (int index = 0; index < 29; ++index) {
        for (final List<BooleanState> states : sensorsStates) {
          states.subList(index, states.size()).equals(
            repository.findNext(states.get(index), states.size() - index - 1)
          ) == true
          
          states.get(index + 1).equals(
            repository.findNext(states.get(index))
          ) == true
        }
      }
  }
  
  def "it allows you to get all states that was emitted by a given sensor" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<BooleanState> repository = new LocalTimeSeriesRepository<BooleanState>(
        new LocalEntityManager(), BooleanState.class
      )
      final List<List<BooleanState>> sensorsStates = generateUpDownSensors(10, 30)
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get all states that was emitted by a given sensor"
      for (final List<BooleanState> states : sensorsStates) {
        states.equals(repository.findAll(
          ApplicationEntityReference.of(
            states.get(0).getSensor()
          )
        ))
      }
  }
  
  def "it allows you to get a slice of states that was emitted by a given sensor" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<BooleanState> repository = new LocalTimeSeriesRepository<BooleanState>(
        new LocalEntityManager(), BooleanState.class
      )
      final List<List<BooleanState>> sensorsStates = generateUpDownSensors(10, 30)
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get a slice of states that was emitted by a given sensor"
      for (final List<BooleanState> states : sensorsStates) {
        for (int size = 2; size < 8; ++size) {
          for (int offset = 0; offset < 10; ++offset) {
            states.subList(
              offset, 
              offset + size
            ).equals(
              repository.find(
                ApplicationEntityReference.of(states.get(0).getSensor()),
                offset, 
                size
              )
            ) == true
          }
        }
      }
  }
  
  def "it allows you to find a state of a given sensor with a particular correlation" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<ActivityState> repository = new LocalTimeSeriesRepository<>(
        new LocalEntityManager(), ActivityState.class
      )
      final List<List<ActivityState>> sensorsStates = generateActivitySensors(
        (String[]) ['first', 'second', 'third']
      )
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        repository.getParent().add(x.get(0).getCorrelation("start").getSensor())
        repository.getParent().addAll(x.get(0).getCorrelation("start").getSensor().getStates())
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get a state of a given sensor with a particular correlation"
      for (int index = 0; index < 3; ++index) {
        for (final ActivityState state : sensorsStates.get(index)) {
          repository.findWithCorrelation(
            "start", 
            ApplicationEntityReference.of(state.getCorrelation("start")),
            ApplicationEntityReference.of(state.getSensor())
          ).equals(state) == true
          
          repository.findWithCorrelation(
            "end", 
            ApplicationEntityReference.of(state.getCorrelation("end")),
            ApplicationEntityReference.of(state.getSensor())
          ).equals(state) == true
        }
      }
  }
  
  def "it allows you to find all states of with a correlation" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<ActivityState> repository = new LocalTimeSeriesRepository<>(
        new LocalEntityManager(), ActivityState.class
      )
      final List<List<ActivityState>> sensorsStates = generateActivitySensors(
        (String[]) ['first', 'second', 'third']
      )
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        repository.getParent().add(x.get(0).getCorrelation("start").getSensor())
        repository.getParent().addAll(x.get(0).getCorrelation("start").getSensor().getStates())
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to find all states of with a correlation"
      final Set<ActivityState> starts = repository.findWithCorrelation("start");
      final Set<ActivityState> ends = repository.findWithCorrelation("end");
      
      starts.size() == sensorsStates.stream().reduce(0, { a, b -> a + b.size() })
      ends.size() == sensorsStates.stream().reduce(0, { a, b -> a + b.size() })
      
      for (final List<ActivityState> states : sensorsStates) {
        for (final ActivityState state : states) {
          starts.contains(state) == true
        }
      }
  }
  
  def "it allows you to find a state of a given sensor with a bunch of correlations" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<ActivityState> repository = new LocalTimeSeriesRepository<>(
        new LocalEntityManager(), ActivityState.class
      )
      final List<List<ActivityState>> sensorsStates = generateActivitySensors(
        (String[]) ['first', 'second', 'third']
      )
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        repository.getParent().add(x.get(0).getCorrelation("start").getSensor())
        repository.getParent().addAll(x.get(0).getCorrelation("start").getSensor().getStates())
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get a state of a given sensor with a bunch of correlation"
      for (int index = 0; index < 3; ++index) {
        for (final ActivityState state : sensorsStates.get(index)) {
          final Map<String, ApplicationEntityReference<State>> correlations = [
            "start": ApplicationEntityReference.of(state.getCorrelation("start")),
            "end": ApplicationEntityReference.of(state.getCorrelation("end"))
          ]
          
          repository.findWithCorrelations(
            correlations,
            ApplicationEntityReference.of(state.getSensor())
          ).equals(state) == true
        }
      }
  }
  
  def "it allows you to find a state of a given sensor with a correlation with a particular value" () {
    given: "a local time series repository with some states registered in"
      final LocalTimeSeriesRepository<ActivityState> repository = new LocalTimeSeriesRepository<>(
        new LocalEntityManager(), ActivityState.class
      )
      final List<List<ActivityState>> sensorsStates = generateActivitySensors(
        (String[]) ['first', 'second', 'third']
      )
      sensorsStates.forEach({ x ->
        repository.getParent().add(x.get(0).getSensor()) 
        repository.getParent().add(x.get(0).getCorrelation("start").getSensor())
        repository.getParent().addAll(x.get(0).getCorrelation("start").getSensor().getStates())
        randomlyAdd(x, repository) 
      })
      
    expect: "to be able to get a state of a given sensor with a correlation with a particular value"
      for (int index = 0; index < 3; ++index) {
        for (final ActivityState state : sensorsStates.get(index)) {          
          repository.findWithAnyCorrelation(
            ["start", "end"],
            ApplicationEntityReference.of(state.getCorrelation("start")),
            ApplicationEntityReference.of(state.getSensor())
          ).equals(state) == true
          
          repository.findWithAnyCorrelation(
            ["start", "end"],
            ApplicationEntityReference.of(state.getCorrelation("end")),
            ApplicationEntityReference.of(state.getSensor())
          ).equals(state) == true
        }
      }
  }
}
