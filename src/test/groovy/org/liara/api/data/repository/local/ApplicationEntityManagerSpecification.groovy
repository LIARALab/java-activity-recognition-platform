package org.liara.api.data.repository.local

import org.liara.api.data.entity.ApplicationEntity
import org.liara.api.data.entity.Sensor
import org.liara.api.data.entity.reference.ApplicationEntityReference
import org.liara.api.data.entity.state.ValueState
import spock.lang.Specification

import java.time.Duration
import java.time.ZonedDateTime

class ApplicationEntityManagerSpecification
  extends Specification
{
  def <NumericState extends ValueState> List<NumericState> generateStates (
    final Class<NumericState> type,
    final int count
  )
  {
    final List<NumericState> entities = []

    for (int index = 0; index < count; ++index) {
      final NumericState state = type.newInstance(new Object[0])
      state.setEmissionDate(ZonedDateTime.now() + Duration.ofMinutes(index))
      state.setValue(index)
      entities.add(state)
    }

    return entities
  }

  def "it allows you to instantiate an empty manager" () {
    expect: "to instantiate an empty manager when the default constructor is invoked"
    new ApplicationEntityManager().size == 0
  }

  def "it allows you to register new entities" () {
    given: "an empty manager"
    final ApplicationEntityManager manager = new ApplicationEntityManager()

    and: "a list of entities to register"
    final List<ValueState.Integer> entities = generateStates(ValueState.Integer.class, 20)

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
    final List<ValueState.Integer> entities = generateStates(ValueState.Integer.class, 20)

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
    final List<ValueState.Integer> entities = generateStates(ValueState.Integer.class, 20)

    entities.forEach({ x -> manager.merge(x) })

    when: "we clear the repository of its entities"
    manager.clear()

    then: "we expect that the repository is empty"
    manager.size == 0
  }

  def "it allows you to remove all entities of a particular type" () {
    given: "a manager with some entities registered in"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final List<ValueState.Integer> aEntities = generateStates(ValueState.Integer.class, 20)
    final List<ValueState.Double> bEntities = generateStates(ValueState.Double.class, 10)
    final List<ValueState.Byte> cEntities = generateStates(ValueState.Byte.class, 10)
    final List<ValueState> entities = []
    entities.addAll(aEntities)
    entities.addAll(bEntities)
    entities.addAll(cEntities)
    Collections.shuffle(entities)

    entities.forEach({ x -> manager.merge(x) })

    when: "we clear the repository of one type of entities"
    manager.clear(ValueState.Double.class)

    then: "we expect that the repository removed all entities of the given type"
    manager.size == entities.size() - bEntities.size()
    for (final ValueState.Double state : bEntities) {
      !manager.contains(state)
    }
  }

  def "it allows to retrieve all entities of a particular type" () {
    given: "a repository with some entities registered in"
    final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
      new ApplicationEntityManager(), Sensor.class
    )
    final List<Sensor> entities = generateEntities(20)

    repository.addAll(entities)

    when: "we find all entities of the repository"
    final List<ApplicationEntity> results = repository.findAll()

    then: "we expect that all added entities are returned"
    new HashSet<>(entities).equals(new HashSet<>(results)) == true
  }

  def "it allows to retrieve an entity of the repository by using a reference over it" () {
    given: "a repository with some entities registered in"
    final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
      new ApplicationEntityManager(), Sensor.class
    )
    final List<Sensor> entities = generateEntities(20)

    repository.addAll(entities)

    when: "we find one entity by using its reference"
    final ApplicationEntity toGet = entities.get(8)
    final ApplicationEntityReference<ApplicationEntity> reference = ApplicationEntityReference.of(toGet)
    final Optional<ApplicationEntity> returned = repository.find(reference)

    then: "we expect to get that entity"
    returned.isPresent() == true
    returned.get().equals(toGet) == true

    when: "we use a reference over an entity that does not exists in the repository"
    final Optional<ApplicationEntity> emptyResult = repository.find(
      ApplicationEntityReference.of(ApplicationEntity.class, 95648654768543L)
    )

    then: "we expect to get an empty optional"
    emptyResult.isPresent() == false
  }

  def "it allows to check if the repository contains an entity by using its identifier" () {
    expect: "to return true if the entity is registered into the repository, false otherwise"
    final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
      new ApplicationEntityManager(), Sensor.class
    )
    final List<Sensor> entities = generateEntities(20)

    repository.addAll(entities)

    repository.contains(entities.get(5).getIdentifier()) == true
    repository.contains(95648654768543L) == false
  }
}
