package org.liara.api.recognition.sensor.common.virtual.updown.activation

import java.rmi.activation.ActivationMonitor
import java.time.ZoneId
import java.time.ZonedDateTime

import org.liara.api.data.entity.ApplicationEntityReference
import org.liara.api.data.entity.node.Node
import org.liara.api.data.entity.sensor.Sensor
import org.liara.api.data.entity.state.ActivationState
import org.liara.api.data.entity.state.ActivationStateCreationSchema
import org.liara.api.data.entity.state.ActivationStateMutationSchema
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.state.BooleanStateMutationSchema
import org.liara.api.data.entity.state.BooleanStateSnapshot
import org.liara.api.data.entity.state.StateDeletionSchema
import org.liara.api.data.entity.state.StateMutationSchema
import org.liara.api.data.entity.state.handler.LocalActivationStateMutationSchemaHandler
import org.liara.api.data.entity.state.handler.LocalActivationStateSchemaHandler
import org.liara.api.data.entity.state.handler.LocalStateMutationSchemaHandler
import org.liara.api.data.entity.tree.LocalNestedSetTree
import org.liara.api.data.repository.local.ApplicationEntityIdentifiers
import org.liara.api.data.repository.local.LocalActivationsRepository
import org.liara.api.data.repository.local.LocalApplicationEntityRepository
import org.liara.api.data.repository.local.LocalEntityManager
import org.liara.api.data.repository.local.LocalTimeSeriesRepository
import org.liara.api.data.schema.TestSchemaManager
import org.liara.api.event.StateWasCreatedEvent
import org.liara.api.event.StateWasDeletedEvent
import org.liara.api.event.StateWasMutatedEvent
import org.liara.api.event.StateWillBeDeletedEvent
import org.liara.api.recognition.sensor.VirtualSensorRunner
import org.liara.api.recognition.sensor.common.NativeBooleanSensor
import org.liara.api.test.builder.node.NodeBuilder
import org.liara.api.test.builder.sensor.SensorBuilder
import org.liara.api.test.builder.state.BooleanStateBuilder
import org.mockito.Mockito
import org.springframework.lang.NonNull
import spock.lang.Specification

public class UpDownToActivationSensorSpecification 
       extends Specification
{
  def "it emit activations at the initialization in accordance with it's source sensor data" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy() 
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.truthy() afterMinutes 5
        andWith BooleanStateBuilder.truthy() afterMinutes 2
        andWith BooleanStateBuilder.truthy() afterMinutes 3
        andWith BooleanStateBuilder.falsy() afterMinutes 5
        andWith BooleanStateBuilder.falsy() afterMinutes 15
        andWith BooleanStateBuilder.truthy() afterHours 1
        andWith BooleanStateBuilder.falsy() afterMinutes 30
        andWith BooleanStateBuilder.truthy() afterHours 2
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, 
        new UpDownToActivationSensor(
          schemaManager, 
          inputRepository, 
          outputRepository, 
          nodesRepository
        )
      )
      
    when: "we initialize the up-down to activation sensor"
      runner.initialize()
      
    then: "we expect that the underlying handler has emitted activations in accordance with it's source sensor data"      
      schemaManager.getHandledSchemaCount() == 5
      
      //------------------------------------------------------------------------------
      schemaManager[0] instanceof ActivationStateCreationSchema == true
      schemaManager[0, ActivationStateCreationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 0].emittionDate,
        "end": null,
        "correlations[start]": inputRepository[sourceSensor.reference, 0].reference,
        "correlations[end]": null,
        "emittionDate": inputRepository[sourceSensor.reference, 0].emittionDate,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true 
      
      //------------------------------------------------------------------------------
      schemaManager[1] instanceof ActivationStateMutationSchema == true
      schemaManager[1, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 4].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 4].reference
      ]) == true
      
      //------------------------------------------------------------------------------
      schemaManager[2] instanceof ActivationStateCreationSchema == true
      schemaManager[2, ActivationStateCreationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 6].emittionDate,
        "end": null,
        "correlations[start]": inputRepository[sourceSensor.reference, 6].reference,
        "correlations[end]": null,
        "emittionDate": inputRepository[sourceSensor.reference, 6].emittionDate,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
      
      //------------------------------------------------------------------------------
      schemaManager[3] instanceof ActivationStateMutationSchema == true
      schemaManager[3, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 7].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 7].reference
      ]) == true
      
      //------------------------------------------------------------------------------
      schemaManager[4] instanceof ActivationStateCreationSchema == true
      schemaManager[4, ActivationStateCreationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 8].emittionDate,
        "end": null,
        "correlations[start]": inputRepository[sourceSensor.reference, 8].reference,
        "correlations[end]": null,
        "emittionDate": inputRepository[sourceSensor.reference, 8].emittionDate,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
  }
  
  def "it ignore non determinant source sensor emittion" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit undeterminant source data"    
      final List<BooleanState> creations = [
        BooleanStateBuilder.truthy({
          withEmittionDate inputRepository[
            sourceSensor.reference, 0
          ].emittionDate.plusMinutes(10)
        }).build(),
        BooleanStateBuilder.truthy({
          withEmittionDate inputRepository[
            sourceSensor.reference, 0
          ].emittionDate.plusMinutes(15)
        }).build(),
        BooleanStateBuilder.falsy({
          withEmittionDate inputRepository[
            sourceSensor.reference, 0
          ].emittionDate.plusMinutes(50)
        }).build(),
        BooleanStateBuilder.falsy({
          withEmittionDate inputRepository[
            sourceSensor.reference, 0
          ].emittionDate.plusMinutes(40)
        }).build()
      ]
      
      for (int index = 0; index < creations.size(); ++index) {
        sourceSensor.addState(creations.get(index))
        manager.add(creations.get(index))
        runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, creations.get(index)))
      }
      
    then: "we expect that the underlying handler will ignore them"
      schemaManager.getHandledSchemaCount() == 0
  }
  
  def "it extends an activation if a up flag was discovered before another one" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.truthy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.minusMinutes(10)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "start": state.emittionDate,
        "correlations[start]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
  }
  
  
  def "it can also extends infinite activation if a up flag was discovered before another one" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.truthy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.minusMinutes(10)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "start": state.emittionDate,
        "correlations[start]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
  }
  
  def "it truncate an activation if a down flag was discovered in it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.falsy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.plusMinutes(5)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": state.emittionDate,
        "correlations[end]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
  }
  
  def "it create an activation if an up flag was discovered before another down flag and outer of any existing activation" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 40
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.truthy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 2
        ].emittionDate.plusMinutes(5)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateCreationSchema == true
      schemaManager[0, ActivationStateCreationSchema.class].lookLike([
        "start": state.emittionDate,
        "end": inputRepository[sourceSensor.reference, 4].emittionDate,
        "correlations[start]": state.reference,
        "correlations[end]": inputRepository[sourceSensor.reference, 4].reference,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
  }
  
  def "it create an infinite activation if an up flag was discovered without any down flag after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 40
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.truthy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 3
        ].emittionDate.plusMinutes(5)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateCreationSchema == true
      schemaManager[0, ActivationStateCreationSchema.class].lookLike([
        "start": state.emittionDate,
        "end": null,
        "correlations[start]": state.reference,
        "correlations[end]": null,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
  }
  
  def "it terminate infinite activation when a down flag was discovered in it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.falsy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.plusMinutes(10)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": state.emittionDate,
        "correlations[end]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
  }
  
  def "it split an infinite activation in two if a down flag was discovered between two up flags" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.truthy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.falsy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.plusMinutes(10)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 2
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": state.emittionDate,
        "correlations[end]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
    
      schemaManager[1] instanceof ActivationStateCreationSchema == true
      schemaManager[1, ActivationStateCreationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 2].emittionDate,
        "end": null,
        "correlations[start]": inputRepository[sourceSensor.reference, 2].reference,
        "correlations[end]": null,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
  }
  
  def "it split an activation in two if a down flag was discovered between two up flags" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final BooleanState state = BooleanStateBuilder.falsy({
        withEmittionDate inputRepository[
          sourceSensor.reference, 0
        ].emittionDate.plusMinutes(10)
      }).build()
      
      sourceSensor.addState(state)
      manager.add(state)
      runner.getHandler().stateWasCreated(new StateWasCreatedEvent(this, state))
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 2
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": state.emittionDate,
        "correlations[end]": state.reference,
        "state": outputRepository[activationSensor.reference, 0].reference
      ]) == true
    
      schemaManager[1] instanceof ActivationStateCreationSchema == true
      schemaManager[1, ActivationStateCreationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 2].emittionDate,
        "end": inputRepository[sourceSensor.reference, 3].emittionDate,
        "correlations[start]": inputRepository[sourceSensor.reference, 2].reference,
        "correlations[end]": inputRepository[sourceSensor.reference, 3].reference,
        "node": livingRoom.reference,
        "sensor": activationSensor.reference
      ]) == true
  }
  
  def "it extends activation if one of its boundary was moved" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 10
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor,
        new UpDownToActivationSensor(
          schemaManager,
          inputRepository,
          outputRepository,
          nodesRepository
        )
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace some boundary flags"
      final List<BooleanStateMutationSchema> mutations = [
        new BooleanStateMutationSchema(),
        new BooleanStateMutationSchema(),
        new BooleanStateMutationSchema(),
        new BooleanStateMutationSchema()
      ]
      
      mutations[0].setState(inputRepository[sourceSensor.reference, 2])
      mutations[0].setEmittionDate(inputRepository[sourceSensor.reference, 2].emittionDate.minusMinutes(10))
      mutations[1].setState(inputRepository[sourceSensor.reference, 2])
      mutations[1].setEmittionDate(inputRepository[sourceSensor.reference, 2].emittionDate)
      mutations[2].setState(inputRepository[sourceSensor.reference, 4])
      mutations[2].setEmittionDate(inputRepository[sourceSensor.reference, 4].emittionDate.plusMinutes(10))
      mutations[3].setState(inputRepository[sourceSensor.reference, 4])
      mutations[3].setEmittionDate(inputRepository[sourceSensor.reference, 4].emittionDate)
      
      for (int index = 0; index < 4; ++index) {
        final BooleanState flag = inputRepository[mutations[index].state]
        final BooleanStateSnapshot oldValue = flag.snapshot()
        manager.remove(flag)
        flag.identifier = oldValue.identifier
        flag.emittionDate = mutations[index].emittionDate
        manager.merge(flag)
        runner.getHandler().stateWasMutated(
          new StateWasMutatedEvent(this, oldValue, flag)
        )
      }
      
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.getHandledSchemaCount() == 4
      schemaManager[0] instanceof ActivationStateMutationSchema == true
      schemaManager[0, ActivationStateMutationSchema].lookLike([
        "start": mutations[0].emittionDate,
        "correlations[start]": mutations[0].state,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
    
      schemaManager[1] instanceof ActivationStateMutationSchema == true
      schemaManager[1, ActivationStateMutationSchema].lookLike([
        "start": mutations[1].emittionDate,
        "correlations[start]": mutations[1].state,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
  
  def "it act like if a state was created if the mutated state was not an activation boundary" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 10
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace some boundary flags"
      final List<BooleanStateMutationSchema> mutations = [
        new BooleanStateMutationSchema()
      ]
      
      mutations[0].setState(inputRepository[sourceSensor.reference, 3])
      mutations[0].setEmittionDate(inputRepository[sourceSensor.reference, 3].emittionDate.minusMinutes(30))
      mutations[0].setValue(false)
      
      for (int index = 0; index < mutations.size(); ++index) {
        final BooleanState flag = inputRepository[mutations[index].state]
        final BooleanStateSnapshot oldValue = flag.snapshot()
        manager.remove(flag)
        flag.identifier = oldValue.identifier
        flag.emittionDate = mutations[index].emittionDate
        flag.value = mutations[index].value
        manager.merge(flag)
        runner.getHandler().stateWasMutated(
          new StateWasMutatedEvent(this, oldValue, flag)
        )
      }
      
      
    then: "we expect that the underlying handler will act accordingly"
      Mockito.verify(handler).inputStateWasCreated(manager[mutations[0].state])
  }
  
  def "it truncate an activation if its start boundary is moved too far away from its original location and if the activation contains another up flag" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 10
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace a boundary flag of an activation state that contains another up flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 2])
      mutation.setEmittionDate(inputRepository[sourceSensor.reference, 2].emittionDate.minusMinutes(70))
      mutation.setValue(true)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.emittionDate = mutation.emittionDate
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 3].emittionDate,
        "correlations[start]": inputRepository[sourceSensor.reference, 3].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it remove an activation if its start boundary is moved too far away from its original location and if the activation does not contains another up flag" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the last boundary flag of an activation state"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 2])
      mutation.setEmittionDate(inputRepository[sourceSensor.reference, 2].emittionDate.minusMinutes(90))
      mutation.setValue(true)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.emittionDate = mutation.emittionDate
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it extand an activation if its end boundary is moved too far away from its original location and if another down flag is present after the old end" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 3])
      mutation.setEmittionDate(inputRepository[sourceSensor.reference, 3].emittionDate.plusMinutes(30))
      mutation.setValue(false)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.emittionDate = mutation.emittionDate
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 3].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 3].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it merge two activation state if the down flag between them is moved too far away" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 3])
      mutation.setEmittionDate(inputRepository[sourceSensor.reference, 3].emittionDate.plusMinutes(80))
      mutation.setValue(false)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.emittionDate = mutation.emittionDate
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 2].reference
      ]) == true
      schemaManager[1, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 4].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 4].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it merge two activation state if the down flag between them is changed into an up flag" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 3])
      mutation.setValue(true)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 2].reference
      ]) == true
      schemaManager[1, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 5].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 5].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it extend an activation state if its end flag is changed into an up flag and another down flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 3])
      mutation.setValue(true)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 4].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 4].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it truncate an activation state if its start flag is changed into a down flag and another up flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 2])
      mutation.setValue(false)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 3].emittionDate,
        "correlations[start]": inputRepository[sourceSensor.reference, 3].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it delete an activation state if its start flag is changed into a down flag and no other up flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema()
      mutation.setState(inputRepository[sourceSensor.reference, 2])
      mutation.setValue(false)
      
      final BooleanState flag = inputRepository[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      manager.remove(flag)
      flag.identifier = oldValue.identifier
      flag.value = mutation.value
      manager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
      Mockito.verify(handler).inputStateWasCreated(manager[mutation.state])
  }
  
  def "it ignore no-boundary deletion" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      for (int index : [3, 4]) {
        final BooleanState flag = inputRepository[sourceSensor.reference, index]
        runner.getHandler().stateWillBeDeleted(
          new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
        )
        manager.remove(flag)
      }
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.handledSchemaCount == 0
  }
  
  def "it merge two activation if the down flag between them is deleted" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = inputRepository[sourceSensor.reference, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      manager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 2].reference
      ]) == true
      schemaManager[1, ActivationStateMutationSchema.class].lookLike([
        "end": null,
        "decorrelationsMap[end]": true,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
  
  def "it extend an activation if its end flag is removed and another end flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = inputRepository[sourceSensor.reference, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      manager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": inputRepository[sourceSensor.reference, 3].emittionDate,
        "correlations[end]": inputRepository[sourceSensor.reference, 3].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
  
  def "it change a finite activation to an infinite activation if its end flag is removed" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = inputRepository[sourceSensor.reference, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      manager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "end": null,
        "decorrelationsMap[end]": true,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
  
  def "it truncate an activation state if its start flag is removed and another up flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = inputRepository[sourceSensor.reference, 2]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      manager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, ActivationStateMutationSchema.class].lookLike([
        "start": inputRepository[sourceSensor.reference, 2].emittionDate,
        "correlations[start]": inputRepository[sourceSensor.reference, 2].reference,
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
  
  def "it remove an activation state if its start flag is removed and no other up flag exists after it" () {
    given: "an entity manager"
      final LocalEntityManager manager = new LocalEntityManager();
    
    and: "a source up-down sensor initialized with some data"
      final Sensor sourceSensor = SensorBuilder.nativeBoolean({
        withName "up-down"
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }).build()
      
      manager.add(sourceSensor)
      manager.addAll(sourceSensor.getStates())
      
    and: "an house with a living-room that contains the source sensor"
      final LocalNestedSetTree<Node> tree = new LocalNestedSetTree()
      
      final Node livingRoom = NodeBuilder.room({
        withName "living-room"
        withSensor sourceSensor
      }).build()
      
      final Node house = NodeBuilder.house({
        withName "house"
      }).build()
      
      manager.addAll([house, livingRoom])
      house.setTree(tree)
      livingRoom.setTree(tree)
      house.addChild(livingRoom)
      
    and: "a convertion sensor in the living-room that track the source sensor"
      final Sensor activationSensor = SensorBuilder.ofType(
        UpDownToActivationSensor.class, {
          withName "presence-detector"
          withConfiguration new UpDownToActivationSensorConfiguration(
            sourceSensor, livingRoom
          )
        }
      ).build()
      
      manager.add(activationSensor)
      livingRoom.addSensor(activationSensor)
      
    and: "a schema manager and some repositories"
      final TestSchemaManager schemaManager = new TestSchemaManager()
      final LocalTimeSeriesRepository<BooleanState> inputRepository = new LocalTimeSeriesRepository<>(manager, BooleanState.class)
      final LocalActivationsRepository outputRepository = new LocalActivationsRepository(manager)
      final LocalApplicationEntityRepository<Sensor> sensorsRepository = LocalApplicationEntityRepository.of(manager, Sensor.class)
      final LocalApplicationEntityRepository<Node> nodesRepository = LocalApplicationEntityRepository.of(manager, Node.class)
      
      schemaManager.registerHandler(
        ActivationStateCreationSchema.class,
        new LocalActivationStateSchemaHandler(manager)
      )
      
      schemaManager.registerHandler(
        ActivationStateMutationSchema.class,
        new LocalActivationStateMutationSchemaHandler(manager)
      )
      
    and: "a new initialized runner for the conversion sensor"
      final UpDownToActivationSensor handler = Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        inputRepository,
        outputRepository,
        nodesRepository
      ))
      
      final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
        activationSensor, handler
      )
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = inputRepository[sourceSensor.reference, 2]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      manager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager[0, StateDeletionSchema.class].lookLike([
        "state": outputRepository[activationSensor.reference, 1].reference
      ]) == true
  }
}
