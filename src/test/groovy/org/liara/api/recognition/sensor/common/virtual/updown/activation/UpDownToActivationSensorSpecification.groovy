package org.liara.api.recognition.sensor.common.virtual.updown.activation

import java.rmi.activation.ActivationMonitor
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors

import org.liara.api.data.entity.ApplicationEntityReference
import org.liara.api.data.entity.node.Node
import org.liara.api.data.entity.sensor.Sensor
import org.liara.api.data.entity.state.ActivationState
import org.liara.api.data.entity.state.ActivationStateCreationSchema
import org.liara.api.data.entity.state.ActivationStateMutationSchema
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.state.BooleanStateMutationSchema
import org.liara.api.data.entity.state.BooleanStateSnapshot
import org.liara.api.data.entity.state.State
import org.liara.api.data.entity.state.StateDeletionSchema
import org.liara.api.data.entity.state.StateMutationSchema
import org.liara.api.data.entity.state.handler.LocalActivationStateMutationSchemaHandler
import org.liara.api.data.entity.state.handler.LocalActivationStateSchemaHandler
import org.liara.api.data.entity.state.handler.LocalStateMutationSchemaHandler
import org.liara.api.data.entity.tree.LocalNestedSetTree
import org.liara.api.data.repository.local.ApplicationEntityIdentifiers
import org.liara.api.data.repository.local.LocalActivationsRepository
import org.liara.api.data.repository.local.LocalApplicationEntityRepository
import org.liara.api.data.repository.local.LocalBooleanStateRepository
import org.liara.api.data.repository.local.LocalEntityManager
import org.liara.api.data.repository.local.LocalNodeRepository
import org.liara.api.data.repository.local.LocalTimeSeriesRepository
import org.liara.api.data.schema.SchemaManager
import org.liara.api.data.schema.TestSchemaManager
import org.liara.api.event.StateWasCreatedEvent
import org.liara.api.event.StateWasDeletedEvent
import org.liara.api.event.StateWasMutatedEvent
import org.liara.api.event.StateWillBeDeletedEvent
import org.liara.api.recognition.sensor.VirtualSensorHandler
import org.liara.api.recognition.sensor.VirtualSensorRunner
import org.liara.api.recognition.sensor.common.NativeBooleanSensor
import org.liara.api.test.builder.node.NodeBuilder
import org.liara.api.test.builder.sensor.SensorBuilder
import org.liara.api.test.builder.state.BooleanStateBuilder
import org.liara.api.test.builder.state.StateSequenceBuilder
import org.mockito.Mockito
import org.springframework.lang.NonNull
import spock.lang.Specification

public class UpDownToActivationSensorSpecification 
       extends Specification
{
  /**
   * @param sequenceConfigurator
   * @param entityManager
   * @return
   */
  def Node buildingTestHouseWithSourceSequence (
    @NonNull final Closure<?> sequenceConfigurator,
    @NonNull final LocalEntityManager entityManager
  ) {
    final LocalNodeRepository nodes = LocalNodeRepository.from(entityManager)
    
    final Node house = NodeBuilder.house({
      withName "house"
      withTree nodes
      withChild NodeBuilder.room({
        withName "living-room"
        withTree nodes
        withSensor SensorBuilder.nativeBoolean({
          withName "input"
          withRawStates StateSequenceBuilder.create(sequenceConfigurator).build()
        })
      })
    }).buildFor(entityManager)
    
    return house
  }
  
  /**
   * 
   * @param house
   * @param entityManager
   * @param schemaManager
   * @return
   */
  def VirtualSensorRunner buildRunnerForHouse (
    @NonNull final Node house,
    @NonNull final LocalEntityManager entityManager,
    @NonNull final SchemaManager schemaManager
  ) {
    final Node livingRoom = house.getFirstChildWithName("living-room").get()
    final Sensor inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get()
    
    final Sensor outputSensor = SensorBuilder.ofType(
      UpDownToActivationSensor.class, {
        withName "output"
        withConfiguration new UpDownToActivationSensorConfiguration(
          inputSensor, livingRoom
        )
      }
    ).buildFor(entityManager)
    
    livingRoom.addSensor(outputSensor)
    
    schemaManager.registerHandler(
      ActivationStateCreationSchema.class,
      new LocalActivationStateSchemaHandler(entityManager)
    )
    
    schemaManager.registerHandler(
      ActivationStateMutationSchema.class,
      new LocalActivationStateMutationSchemaHandler(entityManager)
    )
    
    final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
      outputSensor,
      Mockito.spy(new UpDownToActivationSensor(
        schemaManager,
        LocalBooleanStateRepository.from(entityManager),
        LocalActivationsRepository.from(entityManager),
        LocalNodeRepository.of(house)
      ))
    )
    
    return runner
  }
  
  /**
   * 
   * @param emitter
   * @param entityManager
   * @param runner
   * @param states
   * @return
   */
  def List<State> emit (
    @NonNull final ApplicationEntityReference<Sensor> emitter,
    @NonNull final LocalEntityManager entityManager,
    @NonNull final VirtualSensorRunner runner,
    @NonNull final List<State> states
  ) {
    for (final State state : states) {
      entityManager[emitter].addState(state)
      entityManager.add(state)
      runner.getHandler()
            .stateWasCreated(new StateWasCreatedEvent(this, state))
    }
    
    return states
  }
  
  /***
   * 
   * @param emitter
   * @param entityManager
   * @param runner
   * @param mutations
   * @return
   */
  def List<State> mutate (
    @NonNull final ApplicationEntityReference<Sensor> emitter,
    @NonNull final LocalEntityManager entityManager,
    @NonNull final VirtualSensorRunner runner,
    @NonNull final List<BooleanStateMutationSchema> mutations
  ) {
    for (final BooleanStateMutationSchema mutation : mutations) {
      final BooleanState flag = entityManager[mutation.state]
      final BooleanStateSnapshot oldValue = flag.snapshot()
      entityManager.remove(flag)
      flag.identifier = oldValue.identifier
      if (mutation.emittionDate != null) flag.emittionDate = mutation.emittionDate
      if (mutation.value != null) flag.value = mutation.value 
      entityManager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
    }
    
    return mutations.stream().map({ x -> entityManager[x.state] }).collect(Collectors.toList());
  }
  
  /**
   * 
   * @return
   */
  def "it emit activations at the initialization in accordance with it's source sensor data" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
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
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "a runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
    when: "we initialize the up-down to activation sensor"
      runner.initialize()
      
    then: "we expect that the underlying handler has emitted activations in accordance with it's source sensor data"      
      schemaManager.getHandledSchemaCount() == 5
      schemaManager.hasHandled([
        [
          "class": ActivationStateCreationSchema.class,
          "start": flags[inputSensor, 0].emittionDate,
          "end": null,
          "correlations[start]": flags[inputSensor, 0].reference,
          "correlations[end]": null,
          "emittionDate": flags[inputSensor, 0].emittionDate,
          "node": livingRoom,
          "sensor": outputSensor
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 4].emittionDate,
          "correlations[end]": flags[inputSensor, 4].reference
        ],
        [
          "class": ActivationStateCreationSchema.class,
          "start": flags[inputSensor, 6].emittionDate,
          "end": null,
          "correlations[start]": flags[inputSensor, 6].reference,
          "correlations[end]": null,
          "emittionDate": flags[inputSensor, 6].emittionDate,
          "node": livingRoom,
          "sensor": outputSensor
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 7].emittionDate,
          "correlations[end]": flags[inputSensor, 7].reference
        ],
        [
          "class": ActivationStateCreationSchema.class,
          "start": flags[inputSensor, 8].emittionDate,
          "end": null,
          "correlations[start]": flags[inputSensor, 8].reference,
          "correlations[end]": null,
          "emittionDate": flags[inputSensor, 8].emittionDate,
          "node": livingRoom,
          "sensor": outputSensor
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it ignore non determinant source sensor emittion" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)

      runner.initialize()
      schemaManager.reset()
      
    when: "we emit undeterminant source data"
      emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(10)
          })
          andWith BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(15)
          })
          andWith BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(50)
          })
          andWith BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(40)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will ignore them"
      schemaManager.getHandledSchemaCount() == 0
  }
  
  /**
   * 
   * @return
   */
  def "it extends an activation if a up flag was discovered before another one" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)

      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 0].emittionDate.minusMinutes(10)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": emitted[0].emittionDate,
          "correlations[start]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it can also extends infinite activation if a up flag was discovered before another one" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 0].emittionDate.minusMinutes(10)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": emitted[0].emittionDate,
          "correlations[start]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it truncate an activation if a down flag was discovered in it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(5)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": emitted[0].emittionDate,
          "correlations[end]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it create an activation if an up flag was discovered before another down flag and outer of any existing activation" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 40
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 2].emittionDate.plusMinutes(5)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateCreationSchema.class,
          "start": emitted[0].emittionDate,
          "end": flags[inputSensor, 4].emittionDate,
          "correlations[start]": emitted[0].reference,
          "correlations[end]": flags[inputSensor, 4].reference,
          "node": livingRoom,
          "sensor": outputSensor
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it create an infinite activation if an up flag was discovered without any down flag after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 30
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 40
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy({
            withEmittionDate flags[inputSensor, 3].emittionDate.plusMinutes(5)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateCreationSchema.class,
          "start": emitted[0].emittionDate,
          "end": null,
          "correlations[start]": emitted[0].reference,
          "correlations[end]": null,
          "node": livingRoom,
          "sensor": outputSensor
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it terminate infinite activation when a down flag was discovered in it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
          at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(10)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.getHandledSchemaCount() == 1
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": emitted[0].emittionDate,
          "correlations[end]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it split an infinite activation in two if a down flag was discovered between two up flags" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
          at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.truthy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(10)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 2
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": emitted[0].emittionDate,
          "correlations[end]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ],
        [
          "class": ActivationStateCreationSchema.class,
          "start": flags[inputSensor, 2].emittionDate,
          "end": null,
          "correlations[start]": flags[inputSensor, 2].reference,
          "correlations[end]": null,
          "node": livingRoom,
          "sensor": outputSensor
        ]
      ])
  }
  
  /**
   * 
   * @return
   */
  def "it split an activation in two if a down flag was discovered between two up flags" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit a new up flag before another one"
      final List<State> emitted = emit(
        inputSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.falsy({
            withEmittionDate flags[inputSensor, 0].emittionDate.plusMinutes(10)
          })
        }).build()
      )
      
    then: "we expect that the underlying handler will extand its emitted activation"
      schemaManager.getHandledSchemaCount() == 2
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": emitted[0].emittionDate,
          "correlations[end]": emitted[0].reference,
          "state": outputs[outputSensor, 0].reference
        ],
        [
          "class": ActivationStateCreationSchema.class,
          "start": flags[inputSensor, 2].emittionDate,
          "end": flags[inputSensor, 3].emittionDate,
          "correlations[start]": flags[inputSensor, 2].reference,
          "correlations[end]": flags[inputSensor, 3].reference,
          "node": livingRoom,
          "sensor": outputSensor
        ]
      ])
  }
  
  /**
   * 
   * @return
   */
  def "it extends activation if one of its boundary was moved" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 10
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace some boundary flags"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 2].reference,
          "emittionDate": flags[inputSensor, 2].emittionDate.minusMinutes(10)
        ],
        [
          "state": flags[inputSensor, 2].reference,
          "emittionDate": flags[inputSensor, 2].emittionDate
        ],
        [
          "state": flags[inputSensor, 4].reference,
          "emittionDate": flags[inputSensor, 4].emittionDate.plusMinutes(10)
        ],
        [
          "state": flags[inputSensor, 4].reference,
          "emittionDate": flags[inputSensor, 4].emittionDate
        ],
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )      
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.handledSchemaCount == 4
      
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": mutations[0].emittionDate,
          "correlations[start]": mutations[0].state,
          "state": outputs[outputSensor, 1].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "start": mutations[1].emittionDate,
          "correlations[start]": mutations[1].state,
          "state": outputs[outputSensor, 1].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": mutations[2].emittionDate,
          "correlations[end]": mutations[2].state,
          "state": outputs[outputSensor, 1].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": mutations[3].emittionDate,
          "correlations[end]": mutations[3].state,
          "state": outputs[outputSensor, 1].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it act like if a state was created if the mutated state was not an activation boundary" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 10
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace some boundary flags"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 3].reference,
          "emittionDate": flags[inputSensor, 3].emittionDate.minusMinutes(30),
          "value": false
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it truncate an activation if its start boundary is moved too far away from its original location and if the activation contains another up flag" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
          with BooleanStateBuilder.truthy()
          at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
          andWith BooleanStateBuilder.falsy() afterMinutes 60
          andWith BooleanStateBuilder.truthy() afterMinutes 60
          andWith BooleanStateBuilder.truthy() afterMinutes 10
          andWith BooleanStateBuilder.falsy() afterMinutes 20
          andWith BooleanStateBuilder.truthy() afterMinutes 60
          andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace a boundary flag of an activation state that contains another up flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 2].reference,
          "emittionDate": flags[inputSensor, 2].emittionDate.minusMinutes(70),
          "value": false 
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": flags[inputSensor, 3].emittionDate,
          "correlations[start]": flags[inputSensor, 3].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it remove an activation if its start boundary is moved too far away from its original location and if the activation does not contains another up flag" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we displace the last boundary flag of an activation state"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 2].reference,
          "emittionDate": flags[inputSensor, 2].emittionDate.minusMinutes(90),
          "value": true
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it extand an activation if its end boundary is moved too far away from its original location and if another down flag is present after the old end" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 10
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 3].reference,
          "emittionDate": flags[inputSensor, 3].emittionDate.plusMinutes(30),
          "value": false
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 3].emittionDate,
          "correlations[end]": flags[inputSensor, 3].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it merge two activation state if the down flag between them is moved too far away" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 3].reference,
          "emittionDate": flags[inputSensor, 3].emittionDate.plusMinutes(80),
          "value": false
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 2].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 4].emittionDate,
          "correlations[end]": flags[inputSensor, 4].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it merge two activation state if the down flag between them is changed into an up flag" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 3].reference,
          "value": true
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 2].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 5].emittionDate,
          "correlations[end]": flags[inputSensor, 5].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it extend an activation state if its end flag is changed into an up flag and another down flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 3].reference,
          "value": true
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 4].emittionDate,
          "correlations[end]": flags[inputSensor, 4].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it truncate an activation state if its start flag is changed into a down flag and another up flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 2].reference,
          "value": false
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": flags[inputSensor, 3].emittionDate,
          "correlations[start]": flags[inputSensor, 3].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it delete an activation state if its start flag is changed into a down flag and no other up flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we displace the end boundary flag of an activation state after another down flag"
      final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
        [
          "state": flags[inputSensor, 2].reference,
          "value": false
        ]
      ])
      
      final List<BooleanState> results = mutate(
        inputSensor, entityManager, runner, mutations
      )
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
      Mockito.verify(runner.getHandler() as UpDownToActivationSensor)
             .inputStateWasCreated(entityManager[mutations[0].state])
  }
  
  /**
   * 
   * @return
   */
  def "it ignore no-boundary deletion" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
     
    when: "we remove non-boundary flags"
      for (int index : [3, 4]) {
        final BooleanState flag = flags[inputSensor, index]
        runner.getHandler().stateWillBeDeleted(
          new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
        )
        entityManager.remove(flag)
      }
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.handledSchemaCount == 0
  }
  
  /**
   * 
   * @return
   */
  def "it merge two activation if the down flag between them is deleted" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = flags[inputSensor, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      entityManager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 2].reference
        ],
        [
          "class": ActivationStateMutationSchema.class,
          "end": null,
          "decorrelationsMap[end]": true,
          "state": outputs[outputSensor, 1].reference
        ]
      ])
  }
  
  /**
   * 
   * @return
   */
  def "it extend an activation if its end flag is removed and another end flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we remove non-boundary flags"
      final BooleanState flag = flags[inputSensor, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      entityManager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": flags[inputSensor, 3].emittionDate,
          "correlations[end]": flags[inputSensor, 3].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it change a finite activation to an infinite activation if its end flag is removed" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = flags[inputSensor, 3]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      entityManager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "end": null,
          "decorrelationsMap[end]": true,
          "state": outputs[outputSensor, 1].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it truncate an activation state if its start flag is removed and another up flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 20
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
      
    when: "we remove non-boundary flags"
      final BooleanState flag = flags[inputSensor, 2]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      entityManager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": ActivationStateMutationSchema.class,
          "start": flags[inputSensor, 2].emittionDate,
          "correlations[start]": flags[inputSensor, 2].reference,
          "state": outputs[outputSensor, 1].reference
        ]
      ]) == true
  }
  
  /**
   * 
   * @return
   */
  def "it remove an activation state if its start flag is removed and no other up flag exists after it" () {
    given: "an entity manager and a schemaManager"
      final LocalEntityManager entityManager = new LocalEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house with some source data"
      final Node house = buildingTestHouseWithSourceSequence({
        with BooleanStateBuilder.truthy()
        at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
        andWith BooleanStateBuilder.falsy() afterMinutes 60
        andWith BooleanStateBuilder.truthy() afterMinutes 60
        andWith BooleanStateBuilder.falsy() afterMinutes 20
        andWith BooleanStateBuilder.truthy() afterMinutes 60
      }, entityManager)
      
      final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()
     
    and: "an initialized runner for the output sensor that tracks the input sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
     
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      final LocalActivationsRepository outputs = LocalActivationsRepository.from(entityManager)
  
      runner.initialize()
      schemaManager.reset()
    
    when: "we remove non-boundary flags"
      final BooleanState flag = flags[inputSensor, 2]
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
      )
      entityManager.remove(flag)
      
    then: "we expect that the underlying handler will act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[outputSensor, 1].reference
        ]
      ]) == true
  }
}
