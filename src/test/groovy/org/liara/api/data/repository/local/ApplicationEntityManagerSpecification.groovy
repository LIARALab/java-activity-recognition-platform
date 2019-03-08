package org.liara.api.data.repository.local

import org.liara.api.data.entity.ApplicationEntity
import org.liara.api.data.entity.state.*
import spock.lang.Specification

import java.time.Duration
import java.time.ZonedDateTime

class ApplicationEntityManagerSpecification
  extends Specification
{
  def <ValueType extends Number, StateType extends ValueState<ValueType>> List<StateType> generateStates (
    final Class<StateType> type,
    final int count
  )
  {
    final List<StateType> entities = []

    for (int index = 0; index < count; ++index) {
      final StateType state = type.newInstance(new Object[0])
      state.setEmissionDate(ZonedDateTime.now() + Duration.ofMinutes(index))
      state.setValue(state.getType().newInstance(index.toString()))
      entities.add(state)
    }

    return entities
  }

  def "it allows you to instantiate an empty manager" () {
    expect: "to instantiate an empty manager when the default constructor is invoked"
    new ApplicationEntityManager().size == 0
    new ApplicationEntityManager().findAll() == []
  }

  def "it allows you to register new entities" () {
    given: "an empty manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    and: "a list of entities to register"
    final List<IntegerValueState> entities = generateStates(IntegerValueState.class, 20)

    when: "we register new entities into the given manager"
    entities.forEach({ x -> manager.merge(x) })

    then: "we expect that all added entities was registered into the given manager"
    manager.size == entities.size()
    for (final ApplicationEntity entity : entities) {
      manager.contains(entity)
    }
  }

  def "it allows you to remove entities" () {
    given: "a manager with some entities registered in"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final List<IntegerValueState> entities = generateStates(IntegerValueState.class, 20)

    entities.forEach({ x -> manager.merge(x) })

    and: "a selection of entities to remove"
    final List<ApplicationEntity> toRemove = [1, 5, 8, 9, 12, 19].collect({ x -> entities.get(x) })

    when: "we remove entities from the given manager"
    toRemove.forEach({ x -> manager.remove(x) })

    then: "we expect that all removed entities was unregistered from the given manager"
    manager.size == entities.size() - toRemove.size()
    for (final ApplicationEntity entity : toRemove) {
      !manager.contains(entity)
    }
  }

  def "it allows you to remove all of its entities" () {
    given: "a manager with some entities registered in"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final List<IntegerValueState> entities = generateStates(IntegerValueState.class, 20)

    entities.forEach({ x -> manager.merge(x) })

    when: "we clear the repository of its entities"
    manager.clear()

    then: "we expect that the repository is empty"
    manager.size == 0
  }

  def "it allows you to remove all entities of a particular type" () {
    given: "a manager with some entities registered in"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final Map<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entities = [
      (IntegerValueState.class): generateStates(IntegerValueState.class, 23),
      (DoubleValueState.class) : generateStates(DoubleValueState.class, 5),
      (ShortValueState.class)  : generateStates(ShortValueState.class, 3)
    ]

    entities.values().stream().flatMap({ x -> x.stream() })
            .forEach({ x -> manager.merge(x) })

    when: "we clear the repository of one type of entities"
    manager.clear(DoubleValueState.class)

    then: "we expect that the repository removed all entities of the given type"
    manager.size == entities[IntegerValueState.class].size() + entities[ShortValueState.class].size()
    for (final ApplicationEntity entity : entities[DoubleValueState.class]) {
      !manager.contains(entity)
    }
  }

  def "it allows to retrieve all entities of a given type" () {
    given: "a manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    when: "we add entities of multiple types"
    final Map<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entities = [
      (IntegerValueState.class): generateStates(IntegerValueState.class, 23),
      (DoubleValueState.class) : generateStates(DoubleValueState.class, 5),
      (ShortValueState.class)  : generateStates(ShortValueState.class, 3)
    ]

    entities.values().stream().flatMap({ x -> x.stream() })
            .forEach({ x -> manager.merge(x) })

    then: "we expect that we can retrieve collections of entities of a given type"
    for (
      final Map.Entry<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entry : entities.entrySet()
    ) {
      new HashSet<>(manager.findAll(entry.key)) == new HashSet<>(entry.value)
    }
  }

  def "it allows to retrieve an entity of by using its identifier" () {
    given: "a manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    when: "we add entities of multiple types"
    final Map<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entities = [
      (IntegerValueState.class): generateStates(IntegerValueState.class, 23),
      (DoubleValueState.class) : generateStates(DoubleValueState.class, 5),
      (ShortValueState.class)  : generateStates(ShortValueState.class, 3)
    ]

    entities.values().stream().flatMap({ x -> x.stream() })
            .forEach({ x -> manager.merge(x) })

    then: "we expect to be able to get entities by using their references"
    final Iterator<? extends ApplicationEntity> entitiesToTest = entities.values()
                                                                         .stream()
                                                                         .flatMap({ x -> x.stream() })
                                                                         .iterator()
    while (entitiesToTest.hasNext()) {
      final ApplicationEntity toTest = entitiesToTest.next()
      manager.find(toTest.class, toTest.identifier).get() == toTest
    }
  }

  def "it return an empty optional when we try to find an entity with an unknown identifier" () {
    given: "a manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    when: "we add entities of multiple types"
    final Map<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entities = [
      (IntegerValueState.class): generateStates(IntegerValueState.class, 23),
      (DoubleValueState.class) : generateStates(DoubleValueState.class, 5),
      (ShortValueState.class)  : generateStates(ShortValueState.class, 3)
    ]

    entities.values().stream().flatMap({ x -> x.stream() })
            .forEach({ x -> manager.merge(x) })

    then: "we expect to get an empty optional if we search for an _metamodel that does not exists"
    !manager.find(ByteValueState.class, 5).present
    !manager.find(DoubleValueState.class, 256).present
  }

  def "it allows to check if an entity is registered by using its identifier" () {
    given: "a manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    when: "we add entities of multiple types"
    final Map<Class<? extends ApplicationEntity>, List<? extends ApplicationEntity>> entities = [
      (IntegerValueState.class): generateStates(IntegerValueState.class, 23),
      (DoubleValueState.class) : generateStates(DoubleValueState.class, 5),
      (ShortValueState.class)  : generateStates(ShortValueState.class, 3)
    ]

    entities.values().stream().flatMap({ x -> x.stream() })
            .forEach({ x -> manager.merge(x) })

    then: "we expect to be able to check if an _metamodel is registered by using its reference"
    final Iterator<? extends ApplicationEntity> entitiesToTest = entities.values()
                                                                         .stream()
                                                                         .flatMap({ x -> x.stream() })
                                                                         .iterator()

    while (entitiesToTest.hasNext()) {
      final ApplicationEntity toTest = entitiesToTest.next()
      manager.contains(toTest.class, toTest.identifier)
    }

    !manager.contains(ByteValueState.class, 5)
    !manager.contains(DoubleValueState.class, 256)
  }
}
