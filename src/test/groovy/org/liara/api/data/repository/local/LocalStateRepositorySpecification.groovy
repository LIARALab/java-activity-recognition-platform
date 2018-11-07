package org.liara.api.data.repository.local

import org.liara.api.data.entity.Node
import org.liara.api.data.entity.Sensor
import org.liara.api.data.entity.reference.ApplicationEntityReference
import org.liara.api.data.entity.state.ActivityState
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.state.State
import org.liara.api.data.tree.NestedSetCoordinates
import org.liara.api.recognition.sensor.common.NativeBooleanSensor
import spock.lang.Specification

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors

class LocalStateRepositorySpecification
  extends Specification
{
  static final ORDER_BY_EMITTION_DATE = {
    final State left, final State right -> return left.emissionDate <=> right.emissionDate
  }

  Node generateRootNode () {
    final Node node = new Node()

    node.name = "root"
    node.type = "group"
    node.coordinates = new NestedSetCoordinates(1, 2, 1)

    return node
  }


  Set<BooleanState> randomUpDownStates (final ZonedDateTime start, final int count) {
    final Random random = new Random()
    final HashSet<BooleanState> result = []

    Duration totalDuration = Duration.ofMinutes(0)

    for (int index = 0; index < count; ++index) {
      final BooleanState state = new BooleanState()
      state.value = random.nextBoolean()
      state.emissionDate = start + totalDuration

      totalDuration += Duration.ofMinutes(random.nextInt(98) + 2)

      result.add(state)
    }

    return result
  }

  boolean valueOf (final BooleanState state) {
    return state != null && state.value != null && state.value
  }

  Set<Sensor<BooleanState>> randomUpDownSensors (
    final int sensors, 
    final int states
  ) {
    final Set<Sensor> result = new HashSet<>()
    final Node node = generateRootNode()

    for (int index = 0; index < sensors; ++index) {
      final Sensor sensor = new Sensor()
      sensor.typeClass = NativeBooleanSensor.class
      sensor.name = "updown-$index"
      sensor.node = node
      sensor.native = true
      sensor.states = randomUpDownStates(
        ZonedDateTime.of(2012, 8, 12, 5, index, 00, 00, ZoneId.systemDefault()),
        states
      )

      result.add(sensor)
    }

    return result
  }

  Set<ActivityState> toActivities (
    final Set<BooleanState> flags,
    final String tag
  )
  {
    final List<ActivityState> result = new ArrayList<>()
    final Iterator<BooleanState> flagIterator = new ArrayList<>(flags).sort(ORDER_BY_EMITTION_DATE).iterator()

    BooleanState last = null
    ActivityState currentActivity = null

    while (flagIterator.hasNext()) {
      BooleanState next = flagIterator.next()

      if (!valueOf(last) && valueOf(next)) {
        currentActivity = new ActivityState()
        currentActivity.tag = tag
        currentActivity.emissionDate = next.emissionDate
        currentActivity.start = next.emissionDate
        currentActivity.correlate("start", next)
        result.add(currentActivity)
      } else if (valueOf(last) && !valueOf(next)) {
        currentActivity.end = next.emissionDate
        currentActivity.correlate("end", next)
      }

      last = next
    }

    return result
  }

  Set<Sensor<ActivityState>> randomActivitySensors (
    final String[] tags
  ) {
    final Set<Sensor<ActivityState>> result = new HashSet<>()
    final Iterator<Sensor<BooleanState>> sources = randomUpDownSensors(tags.length, 40).iterator()
    
    for (int index = 0; index < tags.length; ++index) {
      final Sensor<BooleanState> source = sources.next()
      final Sensor<ActivityState> sensor = new Sensor<>()
      sensor.name = "activity-${index}"
      sensor.native = false
      sensor.node = source.node
      sensor.states = toActivities(source.getStates(), tags[index])
      result.add(sensor)
    }

    return result
  }
  
  def <StoredState extends State> void randomlyAdd (
    final Collection<StoredState> states,
    final LocalStateRepository<StoredState> repository
  ) {
    final List<StoredState> toAdd = new ArrayList<>(states)
    Collections.shuffle(toAdd)
    repository.addAll(toAdd)
  }
  
  def "it allows you to get states that was emitted before another state" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<BooleanState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), BooleanState.class
      )
    final Set<Sensor<BooleanState>> sensors = randomUpDownSensors(10, 30)

    when: "we register all states to the given repository"
    repository.parent.add(sensors.iterator().next().getNode())
    repository.parent.addAll(sensors)
    for (final Sensor sensor : sensors) {
      randomlyAdd(sensor.states, repository)
    }

    then: "we expect to be able to get the previous emission(s) of a given time series"
    for (final Sensor sensor : sensors) {
      final List<BooleanState> states = new ArrayList<>(sensor.states)
      Collections.sort(states, ORDER_BY_EMITTION_DATE)

      for (int index = 1; index < 30; ++index) {
        states.subList(0, index) == repository.findPrevious(states.get(index), index)
        states.get(index - 1) == repository.findPrevious(states.get(index)).get()
        }
      }
  }
  
  def "it allows you to get states that was emitted after another state" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<BooleanState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), BooleanState.class
      )
    final Set<Sensor<BooleanState>> sensors = randomUpDownSensors(10, 30)

    when: "we register all states to the given repository"
    repository.parent.add(sensors.iterator().next().getNode())
    repository.parent.addAll(sensors)
    for (final Sensor sensor : sensors) {
      randomlyAdd(sensor.states, repository)
    }

    then: "we expect to be able to get the n next emittions of a given time series"
    for (final Sensor<BooleanState> sensor : sensors) {
      final List<BooleanState> states = new ArrayList<>(sensor.getStates()).sort(ORDER_BY_EMITTION_DATE)
      for (int index = 0; index < states.size() - 1; ++index) {
        states.subList(index, states.size()) == repository.findNext(states.get(index), states.size() - index - 1)
        states.get(index + 1) == repository.findNext(states.get(index))
        }
      }
  }
  
  def "it allows you to get all states that was emitted by a given sensor" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<BooleanState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), BooleanState.class
    )
    final Set<Sensor<BooleanState>> sensors = randomUpDownSensors(10, 30)

    when: "we register all states to the given repository"
    repository.parent.add(sensors.iterator().next().getNode())
    repository.parent.addAll(sensors)
    for (final Sensor sensor : sensors) {
      randomlyAdd(sensor.states, repository)
    }

    then: "we expect to be able to get all states that was emitted by a given sensor"
    for (final Sensor<BooleanState> sensor : sensors) {
      final List<BooleanState> states = new ArrayList<>(sensor.getStates()).sort(ORDER_BY_EMITTION_DATE)
      states == repository.findAll(sensor.getReference())
    }
  }
  
  def "it allows you to get a slice of states that was emitted by a given sensor" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<BooleanState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), BooleanState.class
    )
    final Set<Sensor<BooleanState>> sensors = randomUpDownSensors(10, 30)

    when: "we register all states into the given repository"
    repository.parent.add(sensors.iterator().next().getNode())
    repository.parent.addAll(sensors)
    for (final Sensor sensor : sensors) {
      randomlyAdd(sensor.states, repository)
    }

    then: "we expect to be able to get a slice of states that was emitted by a given sensor"
    for (final Sensor<BooleanState> sensor : sensors) {
      final List<BooleanState> states = new ArrayList<>(sensor.getStates()).sort(ORDER_BY_EMITTION_DATE)
      for (int size = 2; size < 8; ++size) {
        for (int offset = 0; offset < 10; ++offset) {
          states.subList(offset, offset + size) == repository.find(sensor.getReference(), offset, size)
        }
      }
    }
  }
  
  def "it allows you to find a state of a given sensor with a particular correlation" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<ActivityState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), ActivityState.class
    )
    final Set<Sensor<ActivityState>> sensors = randomActivitySensors((String[]) ['first', 'second', 'third'])

    when: "we register all states into the given repository"
    final Node node = sensors.iterator().next().getNode()
    repository.parent.add(node)
    repository.parent.addAll(node.sensors)
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("updown")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("activity")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )

    then: "we expect to be able to get a state of a given sensor with a particular correlation"
    for (final Sensor<ActivityState> sensor : sensors) {
      for (final ActivityState state : sensor.getStates()) {
        repository.findWithCorrelation(
          "start",
          state.getCorrelation("start").get().reference,
          sensor.reference
        ) == state

        if (state.hasCorrelation("end")) {
          repository.findWithCorrelation(
            "end",
            state.getCorrelation("end").get().reference,
            sensor.reference
          ) == state
        }
      }
    }
  }
  
  def "it allows you to find all states of with a correlation" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<ActivityState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), ActivityState.class
    )
    final Set<Sensor<ActivityState>> sensors = randomActivitySensors((String[]) ['first', 'second', 'third'])

    when: "we register all states into the given repository"
    final Node node = sensors.iterator().next().getNode()
    repository.parent.add(node)
    repository.parent.addAll(node.sensors)
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("updown")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("activity")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )

    then: "we expect to be able to find all states of with a correlation"
    final Set<ActivityState> allStates = sensors.stream().map(
      {
        Sensor sensor -> sensor.getStates()
      }
    ).reduce(new HashSet<>(), { a, b -> a.addAll(b); return a })

    repository.findWithCorrelation("start") == allStates.stream().filter(
      {
        ActivityState state -> state.hasCorrelation("start")
      }
    ).collect(Collectors.toSet())

    repository.findWithCorrelation("end") == allStates.stream().filter(
      {
        ActivityState state -> state.hasCorrelation("end")
      }
    ).collect(Collectors.toSet())
  }
  
  def "it allows you to find a state of a given sensor with a bunch of correlations" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<ActivityState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), ActivityState.class
    )
    final Set<Sensor<ActivityState>> sensors = randomActivitySensors((String[]) ['first', 'second', 'third'])

    when: "we register all states into the given repository"
    final Node node = sensors.iterator().next().getNode()
    repository.parent.add(node)
    repository.parent.addAll(node.sensors)
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("updown")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("activity")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )

    then: "we expect to be able to get a state of a given sensor with a bunch of correlation"
    for (final Sensor<ActivityState> sensor : sensors) {
      for (final ActivityState state : sensor.states) {
        final Map<String, ApplicationEntityReference<? extends State>> correlations = [
          "start": state.getCorrelation("start").get().reference,
          "end"  : state.getCorrelation("end").get().reference
        ]

        repository.findWithCorrelations(correlations, sensor.reference) == state
      }
    }
  }
  
  def "it allows you to find a state of a given sensor with a correlation with a particular value" () {
    given: "a local time series repository with some states registered in"
    final LocalStateRepository<ActivityState> repository = LocalStateRepository.create(
      new ApplicationEntityManager(), ActivityState.class
    )
    final Set<Sensor<ActivityState>> sensors = randomActivitySensors((String[]) ['first', 'second', 'third'])

    when: "we register all states into the given repository"
    final Node node = sensors.iterator().next().getNode()
    repository.parent.add(node)
    repository.parent.addAll(node.sensors)
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("updown")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )
    node.sensors.stream().filter(
      {
        Sensor sensor -> sensor.name.startsWith("activity")
      }
    ).each(
      {
        Sensor sensor -> randomlyAdd(sensor.states, repository)
      }
    )

    then: "we expect to be able to get a state of a given sensor with a correlation with a particular value"
    for (final Sensor<ActivityState> sensor : sensors) {
      for (final ActivityState state : sensor.getStates()) {
          repository.findWithAnyCorrelation(
            ["start", "end"],
            state.getCorrelation("start").get().reference,
            state.getSensor().reference
          ) == state

        if (state.hasCorrelation("end")) {
          repository.findWithAnyCorrelation(
            ["start", "end"],
            state.getCorrelation("end").get().reference,
            state.getSensor().reference
          ) == state
        }
        }
      }
  }
}
