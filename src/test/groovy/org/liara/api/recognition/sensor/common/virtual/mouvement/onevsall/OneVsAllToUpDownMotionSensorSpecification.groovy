package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall

import org.liara.api.data.entity.Node
import org.liara.api.data.entity.Sensor
import org.liara.api.data.entity.reference.ApplicationEntityReference
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.state.State
import org.liara.api.data.handler.LocalBooleanStateCreationSchemaHandler
import org.liara.api.data.handler.LocalBooleanStateMutationSchemaHandler
import org.liara.api.data.repository.local.ApplicationEntityManager
import org.liara.api.data.repository.local.LocalBooleanStateRepository
import org.liara.api.data.repository.local.LocalNodeRepository
import org.liara.api.data.repository.local.LocalSensorRepository
import org.liara.api.data.schema.*
import org.liara.api.event.StateWasCreatedEvent
import org.liara.api.event.StateWasMutatedEvent
import org.liara.api.event.StateWillBeDeletedEvent
import org.liara.api.recognition.sensor.VirtualSensorRunner
import org.liara.api.test.builder.node.NodeBuilder
import org.liara.api.test.builder.sensor.SensorBuilder
import org.liara.api.test.builder.state.BooleanStateBuilder
import org.liara.api.test.builder.state.StateSequenceBuilder
import org.mockito.Mockito
import org.springframework.lang.NonNull
import spock.lang.Specification

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors

class OneVsAllToUpDownMotionSensorSpecification
       extends Specification
{
  Node buildTestHouse (
    @NonNull final ApplicationEntityManager entityManager,
    @NonNull final List<String> rooms
  ) {
    final LocalNodeRepository nodes = LocalNodeRepository.from(entityManager)
    
  
    final Node house = NodeBuilder.house({
      withName "house"
      withTree nodes
      for (final String room : rooms) {
        withChild NodeBuilder.room({
          withName room
          withTree nodes
          withSensor SensorBuilder.motion({
            withName "input"
          })
        })
      }
    }).buildFor(entityManager)
    
    return house
  }

  static Duration randomize (@NonNull final Duration duration, final float coef, final long max) {
    final Duration result = Duration.ofSeconds((long)(duration.seconds * coef))
    
    if (result.compareTo(Duration.ofSeconds(max)) <= 0) {
      return result
    } else {
      return Duration.ofSeconds(max)
    }
  }

  void buildTestScenario (
    @NonNull final ApplicationEntityManager entityManager,
    @NonNull final Node house,
    @NonNull final ZonedDateTime startDate,
    @NonNull final List<List<Object>> scenario
  ) {
    Duration totalDuration = scenario[0][1] as Duration
    
    for (int index = 1; index < scenario.size(); ++index) {
      final String previousRoomName = scenario[index - 1][0] as String
      final Duration previousRoomDuration = scenario[index][1] as Duration
      final String nextRoomName = scenario[index][0] as String
      final Duration nextRoomDuration = scenario[index][1] as Duration
      
      final Sensor previousRoomSensor = house.getFirstSensorWithName([previousRoomName, "input"]).get()
      final Sensor nextRoomSensor = house.getFirstSensorWithName([nextRoomName, "input"]).get()
      
      StateSequenceBuilder.create({
         with BooleanStateBuilder.truthy()
         at startDate + totalDuration - OneVsAllToUpDownMotionSensorSpecification.randomize(previousRoomDuration, 0.1, 60)
         andWith BooleanStateBuilder.truthy()
         at startDate + totalDuration - OneVsAllToUpDownMotionSensorSpecification.randomize(previousRoomDuration, 0.08, 50)
         andWith BooleanStateBuilder.truthy()
         at startDate + totalDuration - OneVsAllToUpDownMotionSensorSpecification.randomize(previousRoomDuration, 0.075, 45)
         andWith BooleanStateBuilder.falsy()
         at startDate + totalDuration + OneVsAllToUpDownMotionSensorSpecification.randomize(nextRoomDuration, 0.05, 30)
      }).buildFor(entityManager)
        .forEach({ state -> previousRoomSensor.addState(state) })
      
      StateSequenceBuilder.create({
         with BooleanStateBuilder.truthy()
         at startDate + totalDuration
         andWith BooleanStateBuilder.truthy()
         at startDate + totalDuration + OneVsAllToUpDownMotionSensorSpecification.randomize(nextRoomDuration, 0.04, 20)
         andWith BooleanStateBuilder.truthy()
         at startDate + totalDuration + OneVsAllToUpDownMotionSensorSpecification.randomize(nextRoomDuration, 0.05, 30)
         andWith BooleanStateBuilder.falsy()
         at startDate + totalDuration + OneVsAllToUpDownMotionSensorSpecification.randomize(nextRoomDuration, 0.1, 60)
      }).buildFor(entityManager)
        .forEach({ state -> nextRoomSensor.addState(state) })
      
      totalDuration += nextRoomDuration
    }
  }

  void delete (
    @NonNull final VirtualSensorRunner runner,
    @NonNull final ApplicationEntityManager entityManager,
    @NonNull final Collection<State> states
  ) {
    for (final State state : states) {
      runner.getHandler().stateWillBeDeleted(
        new StateWillBeDeletedEvent(
          this, new StateDeletionSchema(state)
        )
      )
      entityManager.remove(state)
    }
  }
  
  /**
   *
   * @param emitter
   * @param entityManager
   * @param runner
   * @param states
   * @return
   */
  List<State> emit (
    @NonNull final ApplicationEntityReference<Sensor> emitter,
    @NonNull final ApplicationEntityManager entityManager,
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

  VirtualSensorRunner buildRunnerForHouse (
    @NonNull final Node house,
    @NonNull final String room,
    @NonNull final ApplicationEntityManager entityManager,
    @NonNull final SchemaManager schemaManager  
  ) {
    final Node roomNode = house.getFirstChildWithName(room).get()
    final Set<Sensor> validInputsSensors = roomNode.getSensors()
    
    final Sensor outputSensor = SensorBuilder.ofType(
      OneVsAllToUpDownMotionSensor.class, {
        withName "output"
        withConfiguration new OneVsAllToUpDownMotionSensorConfiguration(
          validInputsSensors.stream().map({ x -> x.reference }).collect(Collectors.toList()), 
          Collections.emptyList()
        )
      }
    ).buildFor(entityManager)
    
    roomNode.addSensor(outputSensor)
    
    schemaManager.registerHandler(
      BooleanStateCreationSchema.class,
      new LocalBooleanStateCreationSchemaHandler(entityManager)
    )
    
    schemaManager.registerHandler(
      BooleanStateMutationSchema.class,
      new LocalBooleanStateMutationSchemaHandler(entityManager)
    )
    
    final VirtualSensorRunner runner = VirtualSensorRunner.unbound(
      outputSensor,
      Mockito.spy(new OneVsAllToUpDownMotionSensor(
        schemaManager,
        LocalBooleanStateRepository.from(entityManager),
        LocalSensorRepository.from(entityManager)
      ))
    )
    
    return runner
  }

  List<State> mutate (
    @NonNull final ApplicationEntityManager entityManager,
    @NonNull final VirtualSensorRunner runner,
    @NonNull final List<BooleanStateMutationSchema> mutations
  ) {
    for (final BooleanStateMutationSchema mutation : mutations) {
      final BooleanState flag = entityManager[mutation.state]
      final BooleanState oldValue = flag.clone()
      entityManager.remove(flag)
      flag.identifier = oldValue.identifier
      if (mutation.emittionDate != null) {
        flag.emissionDate = mutation.emittionDate
      }
      if (mutation.value != null) flag.value = mutation.value
      entityManager.merge(flag)
      runner.getHandler().stateWasMutated(
        new StateWasMutatedEvent(this, oldValue, flag)
      )
    }

    return mutations.stream().map({ x -> entityManager[x.state] }).collect(Collectors.toList())
  }
    
  def "it emit boundaries flags from existing sensor data when the virtual sensor is initialized" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house and a scenario for it"
      final Node house = buildTestHouse(
        entityManager, 
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())

      buildTestScenario(
        entityManager, house, startDate,
        [
          ["living-room", Duration.ofMinutes(20)], ["kitchen", Duration.ofMinutes(45)],
          ["living-room", Duration.ofMinutes(5)], ["dining-room", Duration.ofMinutes(30)],
          ["living-room", Duration.ofMinutes(5)], ["kitchen", Duration.ofMinutes(15)],
          ["living-room", Duration.ofMinutes(20)], ["bathroom", Duration.ofMinutes(30)],
          ["living-room", Duration.ofMinutes(5)], ["bedroom", Duration.ofMinutes(8 * 60)],
          ["living-room", Duration.ofMinutes(5)]
        ]
      )
      
    and: "a new runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
    when: "we initialize the sensor"
      runner.initialize()
      
    then: "we expect that the underlying handler has emitted flags in accordance with it's source sensor data"
      schemaManager.handledSchemaCount == 5
      schemaManager.hasHandled([
        [
          "class"             : BooleanStateCreationSchema.class,
          "emittionDate"      : flags[livingRoomSensor, 0].emissionDate,
          "value"             : false,
          "correlations[base]": flags[livingRoomSensor, 4 * 0].reference,
          "sensor"            : outputSensor
        ],
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate + Duration.ofMinutes(20),
          "value": true,
          "correlations[base]": flags[kitchenSensor, 4 * 0].reference,
          "sensor": outputSensor 
        ],
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate + Duration.ofMinutes(20 + 45),
          "value": false,
          "correlations[base]": flags[livingRoomSensor, 4 * 1].reference,
          "sensor": outputSensor 
        ],
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate + Duration.ofMinutes(20 + 45 + 5 + 30 + 5),
          "value": true,
          "correlations[base]": flags[kitchenSensor, 4 * 2].reference,
          "sensor": outputSensor 
        ],
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate + Duration.ofMinutes(20 + 45 + 5 + 30 + 5 + 15),
          "value": false,
          "correlations[base]": flags[livingRoomSensor, 4 * 5].reference,
          "sensor": outputSensor 
        ]
      ]) == true
  }
  
  def "it ignore false flags creation" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house and a scenario for it"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
      buildTestScenario(
        entityManager, house, startDate,
        [
          ["living-room", Duration.ofMinutes(20)], ["kitchen", Duration.ofMinutes(45)],
          ["living-room", Duration.ofMinutes(5)], ["dining-room", Duration.ofMinutes(30)],
          ["living-room", Duration.ofMinutes(5)]
        ]
      )
      
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we emit false flags"
      final StateSequenceBuilder builder = StateSequenceBuilder.create({
        for (int index in (0 .. 20)) {
          with BooleanStateBuilder.falsy()
          at startDate + Duration.ofMinutes(5 * index)
        } 
      })
      
      emit(livingRoomSensor, entityManager, runner, builder.build())
      emit(kitchenSensor, entityManager, runner, builder.build())
      
    then: "we expect that the underlying handler ignore them"
      Mockito.verify(
        runner.getHandler() as OneVsAllToUpDownMotionSensor, Mockito.never()
      ).onMotionStateWasCreated(Mockito.any())
  }
  
  def "it emit an up flag when a valid motion sensor emit a up flag" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house and a scenario for it"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      schemaManager.reset()
      
    when: "a valid motion sensor emit an up flag"      
      emit(
        kitchenSensor, entityManager, runner, 
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate 
        }).build()
      )
      
    then: "we expect that the underlying handler emit a up flag"
      schemaManager.handledSchemaCount == 1
      schemaManager.hasHandled([
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate,
          "value": true,
          "correlations[base]": flags[kitchenSensor, 0].reference,
          "sensor": outputSensor 
        ]
      ]) == true
  }
  
  
  def "it emit flags if they are different from their predecessor or their successor" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house and a scenario for it"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      schemaManager.reset()
      
    when: "we chain flags emittion"
      final List<BooleanState> emittions = []
    
      emittions.addAll emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      emittions.addAll emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate - Duration.ofMinutes(5)
          andWith BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5)
        }).build()
      )
      
      emittions.addAll emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(10)
        }).build()
      )
      
    then: "we expect that the underlying handler emit a up flag"
      schemaManager.handledSchemaCount == 4
      schemaManager.hasHandled([
        [
          "class"             : BooleanStateCreationSchema.class,
          "emittionDate"      : emittions[0].emissionDate,
          "value"             : true,
          "correlations[base]": emittions[0].reference,
          "sensor"            : outputSensor
        ],
        [
          "class"             : BooleanStateCreationSchema.class,
          "emittionDate"      : emittions[1].emissionDate,
          "value"             : false,
          "correlations[base]": emittions[1].reference,
          "sensor"            : outputSensor
        ],
        [
          "class"             : BooleanStateCreationSchema.class,
          "emittionDate"      : emittions[2].emissionDate,
          "value"             : false,
          "correlations[base]": emittions[2].reference,
          "sensor"            : outputSensor
        ],
        [
          "class"             : BooleanStateCreationSchema.class,
          "emittionDate"      : emittions[3].emissionDate,
          "value"             : true,
          "correlations[base]": emittions[3].reference,
          "sensor"            : outputSensor
        ]
      ]) == true
  }
  
  def "it emit a down flag when an invalid motion sensor emit a up flag" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house and a scenario for it"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      schemaManager.reset()
      
    when: "an invalid motion sensor emit an up flag"      
      emit(
        livingRoomSensor, entityManager, runner, 
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate 
        }).build()
      )
      
    then: "we expect that the underlying handler emit a down flag"
      schemaManager.handledSchemaCount == 1
      schemaManager.hasHandled([
        [
          "class": BooleanStateCreationSchema.class,
          "emittionDate": startDate,
          "value": false,
          "correlations[base]": flags[livingRoomSensor, 0].reference,
          "sensor": outputSensor 
        ]
      ]) == true
  }
  
  def "it ignore consecutive valid emittion" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive valid flags"      
      emit(
        kitchenSensor, entityManager, runner, 
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate + Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler does nothing"
      schemaManager.handledSchemaCount == 0
  }
  
  def "it ignore consecutive invalid emittion" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive invalid flags"      
      emit(
        livingRoomSensor, entityManager, runner, 
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate + Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler does nothing"
      schemaManager.handledSchemaCount == 0
  }
  
  def "it move a up flag if another up flag is emitted before him (no content before)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive valid flags"
      def em = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate - Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler does moves the emitted flag"
      schemaManager.handledSchemaCount == 20
    List<Map<String, ?>> handled = []
      
      for (int index in (1..20)) {
        handled.addAll([
          [
            "class": BooleanStateMutationSchema.class,
            "state": flags[outputSensor, 0].reference,
            "emittionDate": startDate - Duration.ofMinutes(5 * index),
            "correlations[base]": flags[kitchenSensor, 20 - index].reference
          ]
        ])
      }
      
      schemaManager.hasHandled(handled) == true
  }
  
  def "it move a up flag if another up flag is emitted before him (with content before)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      
      buildTestScenario(
        entityManager, house, startDate - Duration.ofMinutes(30 * 5),
        [
          ["living-room", Duration.ofMinutes(5)], ["kitchen", Duration.ofMinutes(30)],
          ["living-room", Duration.ofMinutes(5)]
        ]
      )
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive valid flags"
      def em = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate - Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler does moves the emitted flag"
      schemaManager.handledSchemaCount == 20
    List<Map<String, ?>> handled = []
      
      for (int index in (1..20)) {
        handled.addAll([
          [
            "class": BooleanStateMutationSchema.class,
            "state": flags[outputSensor, 3].reference,
            "emittionDate": startDate - Duration.ofMinutes(5 * index),
            "correlations[base]": flags[kitchenSensor, 20 - index + 8].reference
          ]
        ])
      }
      
      schemaManager.hasHandled(handled) == true
  }
  
  def "it move a down flag if another down flag is emitted before him (no content before)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
      
      runner.initialize()
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive invalid flags"
      def em = emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate - Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler move the emitted flag"
      schemaManager.handledSchemaCount == 20
    List<Map<String, ?>> handled = []
      
      for (int index in (1..20)) {
        handled.addAll([
          [
            "class": BooleanStateMutationSchema.class,
            "state": flags[outputSensor, 0].reference,
            "emittionDate": startDate - Duration.ofMinutes(5 * index),
            "correlations[base]": flags[livingRoomSensor, 20 - index].reference
          ]
        ])
      }
      
      schemaManager.hasHandled(handled) == true
  }
  
  def "it move a down flag if another down flag is emitted before him (with content before)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
      
      buildTestScenario(
        entityManager, house, startDate - Duration.ofMinutes(40 * 5),
        [
          ["dining-room", Duration.ofMinutes(5)],
          ["kitchen", Duration.ofMinutes(30)]
        ]
      )
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit consecutive invalid flags"
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          for (int index in (1..20)) {
            with BooleanStateBuilder.truthy()
            at startDate - Duration.ofMinutes(5 * index)
          }
        }).build()
      )
      
    then: "we expect that the underlying handler move the emitted flag"
      schemaManager.handledSchemaCount == 20
    List<Map<String, ?>> handled = []
      
      for (int index in (1..20)) {
        handled.addAll([
          [
            "class": BooleanStateMutationSchema.class,
            "state": flags[outputSensor, 2].reference,
            "emittionDate": startDate - Duration.ofMinutes(5 * index),
            "correlations[base]": flags[livingRoomSensor, 20 - index].reference
          ]
        ])
      }
      
      schemaManager.hasHandled(handled) == true
  }
  
  def "it create two flags if a down flag is discovered between two up flags" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager,
        ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      def kitchenEmittions = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      
    when: "we emit a down flag between two up flags"
      def emitted = emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(7)
        }).build()
      ).get(0)

    then: "we expect that the underlying handler will instantiate two flags"
      schemaManager.handledSchemaCount == 2
      
      schemaManager.hasHandled([
        [
          "class"             : BooleanStateCreationSchema.class,
          "correlations[base]":  emitted.reference,
          "emittionDate"      : emitted.emissionDate,
          "value"             : false
        ],
        [
          "class"             : BooleanStateCreationSchema.class,
          "correlations[base]":  kitchenEmittions[2].reference,
          "emittionDate"      : kitchenEmittions[2].emissionDate,
          "value"             : true
        ]
      ]) == true
  }
  
  def "it act like if a flag was created if an uncorreled up flag is moved" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      def kitchenEmittions = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move an uncorreled up flag"
      final List<BooleanState> mutations = mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 1].reference,
          "emittionDate": flags[kitchenSensor, 1].emissionDate - Duration.ofMinutes(2)
        ])
      ]
      )

    then: "we expect that the underlying handler will instantiate two flags"
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor)
             .onMotionStateWasCreated(mutations[0])
  }
  
  def "it ignore uncorreled flags up to down value change" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      def kitchenEmittions = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move an uncorreled up flag"
      final List<BooleanState> mutations = mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 1].reference,
          "emittionDate": flags[kitchenSensor, 1].emissionDate - Duration.ofMinutes(2),
          "value"       : false
        ])
      ]
      )

    then: "we expect that the underlying handler will instantiate two flags"
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor, Mockito.never())
             .onMotionStateWasCreated(mutations[0])
  }
  
  def "it move boundaries if boundaries are near of their origin" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      def kitchenEmittions = emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move correled flag near from their original location"
      final List<BooleanState> mutations = mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate - Duration.ofMinutes(5)
        ]),
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate
        ]),
        BooleanStateMutationSchema.create([
          "state"       : flags[livingRoomSensor, 0].reference,
          "emittionDate": flags[livingRoomSensor, 0].emissionDate + Duration.ofMinutes(5)
        ]),
        BooleanStateMutationSchema.create([
          "state"       : flags[livingRoomSensor, 0].reference,
          "emittionDate": flags[livingRoomSensor, 0].emissionDate
        ])
      ]
      )

    then: "we expect that the underlying handler will react accordingly"
      schemaManager.handledSchemaCount == 4
      schemaManager.hasHandled([
        [
          "class"       : BooleanStateMutationSchema.class,
          "state"       : flags[outputSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate - Duration.ofMinutes(5)
        ],
        [
          "class"       : BooleanStateMutationSchema.class,
          "state"       : flags[outputSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate
        ],
        [
          "class"       : BooleanStateMutationSchema.class,
          "state"       : flags[outputSensor, 1].reference,
          "emittionDate": flags[livingRoomSensor, 0].emissionDate + Duration.ofMinutes(5)
        ],
        [
          "class"       : BooleanStateMutationSchema.class,
          "state"       : flags[outputSensor, 1].reference,
          "emittionDate": flags[livingRoomSensor, 0].emissionDate
        ]
      ])
  }
  
  def "it delete a boundary flag and recreate it if the flag is moved too far away from its original location (right)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag to far away"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[livingRoomSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate - Duration.ofMinutes(5)
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[1].reference
        ]
      ])
      
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor)
             .onMotionStateWasCreated(flags[livingRoomSensor, 0])
  }
  
  def "it delete a boundary flag and recreate it if the flag is moved too far away from its original location (left)" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5 * 3)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag to far away"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate + Duration.ofMinutes(10 * 3)
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[0].reference
        ]
      ])
      
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor)
             .onMotionStateWasCreated(flags[kitchenSensor, 0])
  }
  
  def "it merge flags if an update made two flags of the same type in sequence" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 10
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[livingRoomSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 1].emissionDate + Duration.ofMinutes(5)
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[1].reference
        ],
        [
          "class": StateDeletionSchema.class,
          "state": outputs[2].reference
        ]
      ])
      
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor)
             .onMotionStateWasCreated(flags[livingRoomSensor, 0])
  }
  
  def "it move a flag, if a correled flag was moved and another flag of the same type exists after it" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 10
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 20
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5)
          andWith BooleanStateBuilder.truthy()
          afterMinutes 20
          andWith BooleanStateBuilder.truthy()
          afterMinutes 20
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 1].reference,
          "emittionDate": flags[kitchenSensor, 3].emissionDate + Duration.ofMinutes(2)
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.hasHandled([
        [
          "class": BooleanStateMutationSchema.class,
          "state": outputs[2].reference,
          "emittionDate": startDate + Duration.ofMinutes(15),
          "correlations[base]": flags[kitchenSensor, 1].reference
        ]
      ]) == true
      
      Mockito.verify(runner.handler as OneVsAllToUpDownMotionSensor)
             .onMotionStateWasCreated(flags[kitchenSensor, 3])
  }
  
  def "it move a flag, if it is the only flag discovered and if its value does not change" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate + Duration.ofMinutes(5)
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 1
      schemaManager.hasHandled([
        [
          "class": BooleanStateMutationSchema.class,
          "state": outputs[0].reference,
          "emittionDate": startDate + Duration.ofMinutes(5)
        ]
      ]) == true
  }
  
  def "it delete a flag, if it is the only flag discovered and if its value change" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we move a correled flag"
      final List<BooleanState> outputs = flags.findAll(outputSensor)
    
      mutate(entityManager, runner, [
        BooleanStateMutationSchema.create([
          "state"       : flags[kitchenSensor, 0].reference,
          "emittionDate": flags[kitchenSensor, 0].emissionDate + Duration.ofMinutes(5),
          "value"       : false
        ])
      ]
      )

    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 1
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[0].reference
        ]
      ]) == true
  }
  
  def "it ignore down flag deletion" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.falsy()
          afterMinutes 10
          andWith BooleanStateBuilder.falsy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(30)
          andWith BooleanStateBuilder.falsy()
          afterMinutes 10
          andWith BooleanStateBuilder.falsy()
          afterMinutes 5
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove down flags"
      delete(runner, entityManager, [
        flags[kitchenSensor, 1], 
        flags[kitchenSensor, 2], 
        flags[livingRoomSensor, 1]  
      ])
      
    then: "we expect that the underlying handler does nothing"
      schemaManager.handledSchemaCount == 0
  }
  
  def "it ignore uncorreled flag deletion" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          andWith BooleanStateBuilder.truthy()
          afterMinutes 10
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(30)
          andWith BooleanStateBuilder.truthy()
          afterMinutes 10
          andWith BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove uncorrelated flags"      
      delete(runner, entityManager, [
        flags[kitchenSensor, 1],
        flags[kitchenSensor, 2],
        flags[kitchenSensor, 3],
        flags[livingRoomSensor, 1],
        flags[livingRoomSensor, 2]
      ])
      
    then: "we expect that the underlying handler does nothing"
      schemaManager.handledSchemaCount == 0
  }
  
  def "it remove a flag if its correlated flag is removed and no valid flag exists near it" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(30)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove some flags"
      final List<State> outputs = flags.findAll(outputSensor)
      
      delete(runner, entityManager, [
        flags[livingRoomSensor, 0],
        flags[kitchenSensor, 0]
      ])
      
    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 2
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[1].reference
        ],
        [
          "class": StateDeletionSchema.class,
          "state": outputs[0].reference
        ]
      ])
  }
  
  def "it move a flag if its correlated flag is removed and another valid flag exists near it" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          with BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(30)
          with BooleanStateBuilder.truthy()
          afterMinutes 5
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove some flags"
      final List<State> outputs = flags.findAll(outputSensor)
      
      delete(runner, entityManager, [
        flags[livingRoomSensor, 0],
        flags[kitchenSensor, 0]
      ])
      
    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 2
      schemaManager.hasHandled([
        [
          "class": BooleanStateMutationSchema.class,
          "state": outputs[1].reference,
          "correlations[base]": flags[livingRoomSensor, 0].reference
        ],
        [
          "class": BooleanStateMutationSchema.class,
          "state": outputs[0].reference,
          "correlations[base]": flags[kitchenSensor, 0].reference
        ]
      ])
  }
  
  def "it remove two flags if a deletion result in the merging of two up flags" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
          with BooleanStateBuilder.truthy()
          afterMinutes 10
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5)
          with BooleanStateBuilder.truthy()
          afterMinutes 40
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove some flags"
      final List<State> outputs = flags.findAll(outputSensor)
      
      delete(runner, entityManager, [
        flags[livingRoomSensor, 0]
      ])
      
    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 2
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[1].reference
        ],
        [
          "class": StateDeletionSchema.class,
          "state": outputs[2].reference
        ]
      ])
  }
  
  
  def "it can remove starting flags when they related flags are removed" () {
    given: "an entity manager and a schema manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()
      final TestSchemaManager schemaManager = new TestSchemaManager()
    
    and: "an house"
      final Node house = buildTestHouse(
        entityManager, ["bathroom", "living-room", "dining-room", "bedroom", "kitchen"]
      )
      
      final ZonedDateTime startDate = ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
  
    and: "a new initialized runner for the one vs all sensor"
      final VirtualSensorRunner runner = buildRunnerForHouse(house, "kitchen", entityManager, schemaManager)
      final ApplicationEntityReference<Sensor> livingRoomSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> kitchenSensor = house.getFirstSensorWithName(["kitchen", "input"]).get().getReference()
      final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["kitchen", "output"]).get().getReference()
      final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
            
      runner.initialize()
      
      emit(
        kitchenSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate
        }).build()
      )
      
      emit(
        livingRoomSensor, entityManager, runner,
        StateSequenceBuilder.create({
          with BooleanStateBuilder.truthy()
          at startDate + Duration.ofMinutes(5)
        }).build()
      )
      
      schemaManager.reset()
      Mockito.reset(runner.handler)
      
    when: "we remove some flags"
      final List<State> outputs = flags.findAll(outputSensor)
      
      delete(runner, entityManager, [
        flags[kitchenSensor, 0]
      ])
      
    then: "we expect that the underlying handler act accordingly"
      schemaManager.handledSchemaCount == 1
      schemaManager.hasHandled([
        [
          "class": StateDeletionSchema.class,
          "state": outputs[0].reference
        ]
      ])
  }
}
