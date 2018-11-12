package org.liara.api.data.repository.local

import org.liara.api.data.entity.ApplicationEntity
import org.liara.api.data.entity.Sensor
import org.liara.api.data.entity.reference.ApplicationEntityReference
import org.liara.api.test.builder.sensor.SensorBuilder
import spock.lang.Specification

class LocalApplicationEntityRepositorySpecification
       extends Specification
{
  List<Sensor> generateEntities (final int count) {
    final SensorBuilder builder = new SensorBuilder()
    final List<Sensor> entities = []
    
    for (int index = 0; index < count; ++index) {
      entities.add(SensorBuilder.nativeBoolean({
        withName "sensor_" + index 
      }).build())
    }
    
    return entities
  }
  
  def "it allows you to instanciate an empty repository for a given entity type" () {
    expect: "to instanciate an empty repository when the default constructor is invoked"
    final LocalApplicationEntityRepository<Sensor> repository = new ApplicationEntityManager().repository(
      LocalApplicationEntityRepository, Sensor.class
      )

    repository.findAll().empty
  }
  
  def "it allows you to register new entities one by one into the repository" () {
    given: "an empty repository"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final LocalApplicationEntityRepository<Sensor> repository = manager.repository(
      LocalApplicationEntityRepository, Sensor.class
      )
      
    and: "a list of entities to register"
      final List<Sensor> entities = generateEntities(20)
      
    when: "we register new entities one by one into the given repository"
    entities.forEach({ x -> manager.merge(x) })
      
    then: "we expect that all added entities was registered into the given repository"
    repository.findAll().size() == entities.size()
      for (final ApplicationEntity entity : entities) {
        repository.find(entity.reference.identifier).present
      }
  }
  
  def "it allows you to register a bunch of entities into the repository" () {
    given: "an empty repository"
    final ApplicationEntityManager manager = new ApplicationEntityManager()
    final LocalApplicationEntityRepository<Sensor> repository = manager.repository(
      LocalApplicationEntityRepository, Sensor.class
      )

    and: "a list of entities to register"
      final List<Sensor> entities = generateEntities(20)
      
    when: "we register all entities into the given repository"
      repository.addAll(entities)
      
    then: "we expect that all added entities was registered into the given repository"
      repository.size() == entities.size()
      for (final ApplicationEntity entity : entities) {
        repository.contains(entity) == true
      }
  }
  
  def "it allows you to register an iterator of entities into the repository" () {
    given: "an empty repository"
      final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
        new ApplicationEntityManager(), Sensor.class
      )
    
    and: "a list of entities to register"
      final List<Sensor> entities = generateEntities(20)
      
    when: "we register new entities as an iterator into the given repository"
      repository.addAll(entities.iterator())
      
    then: "we expect that all added entities was registered into the given repository"
      repository.size() == entities.size()
      for (final ApplicationEntity entity : entities) {
        repository.contains(entity) == true
      }
  }
  
  def "it allows you to remove entities one by one from the repository" () {
    given: "a repository with some entities registered in"
      final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
        new ApplicationEntityManager(), Sensor.class
      )
      final List<Sensor> entities = generateEntities(20)
      
      repository.addAll(entities)
      
    and: "a selection of entities to remove"
      final List<ApplicationEntity> toRemove = [1, 5, 8, 9, 12, 19].collect({ x -> entities.get(x) })
      
    when: "we remove entities one by one from the given repository"
      toRemove.forEach({ x -> repository.remove(x) })
      
    then: "we expect that all removed entities was unregistered from the given repository"
      repository.size() == entities.size() - toRemove.size()  
      for (final ApplicationEntity entity : toRemove) {
        repository.contains(entity) == false
      }
  }
  
  def "it allows you to remove a bunch of entities from the repository" () {
    given: "a repository with some entities registered in"
      final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
        new ApplicationEntityManager(), Sensor.class
      )
      final List<Sensor> entities = generateEntities(20)
      
      repository.addAll(entities)
      
    and: "a selection of entities to remove"
      final List<ApplicationEntity> toRemove = [1, 5, 8, 9, 12, 19].collect({ x -> entities.get(x) })
      
    when: "we remove a bunch of entities from the given repository"
      repository.removeAll(toRemove)
      
    then: "we expect that all removed entities was unregistered from the given repository"
      repository.size() == entities.size() - toRemove.size()  
      for (final ApplicationEntity entity : toRemove) {
        repository.contains(entity) == false
      }
  }
  
  def "it allows you to remove an iterator of entities from the repository" () {
    given: "a repository with some entities registered in"
      final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
        new ApplicationEntityManager(), Sensor.class
      )
      final List<Sensor> entities = generateEntities(20)
      
      repository.addAll(entities)
      
    and: "a selection of entities to remove"
      final List<ApplicationEntity> toRemove = [1, 5, 8, 9, 12, 19].collect({ x -> entities.get(x) })
      
    when: "we remove an iterator of entities from the given repository"
      repository.removeAll(toRemove.iterator())
      
    then: "we expect that all removed entities was unregistered from the given repository"
      repository.size() == entities.size() - toRemove.size()
      for (final ApplicationEntity entity : toRemove) {
        repository.contains(entity) == false
      }
  }
  
  def "it allows you to clear the repository of its entities" () {
    given: "a repository with some entities registered in"
      final LocalApplicationEntityRepository<Sensor> repository = LocalApplicationEntityRepository.from(
        new ApplicationEntityManager(), Sensor.class
      )
      final List<Sensor> entities = generateEntities(20)
      
      repository.addAll(entities)
      
    when: "we clear the repository of its entities"
      repository.clear()
      
    then: "we expect that the repository is empty"
      repository.size() == 0
      repository.findAll().size() == 0
  }
  
  def "it allows to retrieve all entities of the repository" () {
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
