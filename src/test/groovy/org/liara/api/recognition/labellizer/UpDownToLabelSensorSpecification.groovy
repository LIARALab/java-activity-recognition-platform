package org.liara.api.recognition.labellizer

import org.liara.api.data.entity.Sensor
import org.liara.api.data.entity.state.BooleanValueState
import org.liara.api.data.repository.local.ApplicationEntityManager
import org.liara.api.data.repository.local.LocalBooleanValueStateRepository
import org.liara.api.data.repository.local.LocalCorrelationRepository
import org.liara.api.data.repository.local.LocalLabelStateRepository
import org.liara.api.io.APIEventPublisher
import org.liara.api.recognition.sensor.type.ValueSensorType
import org.mockito.Mockito
import spock.lang.Specification

import java.time.ZonedDateTime

class UpDownToLabelSensorSpecification
  extends Specification
{
  UpDownToLabelSensorBuilder getUpDownToLabelSensorBuilder (final ApplicationEntityManager entityManager) {
    final UpDownToLabelSensorBuilder builder = new UpDownToLabelSensorBuilder()
    builder.setCorrelations(entityManager.repository(LocalCorrelationRepository.class))
    builder.setInputs(entityManager.repository(LocalBooleanValueStateRepository.class))
    builder.setOutputs(entityManager.repository(LocalLabelStateRepository.class))
    builder.setPublisher(Mockito.mock(APIEventPublisher.class))

    return builder
  }

  Sensor createSourceSensor (final ApplicationEntityManager entityManager) {
    final Sensor source = new Sensor()
    source.setName("source")
    source.setType(ValueSensorType.STRING.name)
    source.setConfiguration(null)
    source.setNodeIdentifier(null)

    entityManager.merge(source)

    return source
  }

  BooleanValueState truthy (final Sensor sensor, final ZonedDateTime emissionDate) {
    final BooleanValueState result = new BooleanValueState()
    result.setSensorIdentifier(sensor.identifier)
    result.setValue(true)
    result.setEmissionDate(emissionDate)

    return result
  }

  BooleanValueState falsy (final Sensor sensor, final ZonedDateTime emissionDate) {
    final BooleanValueState result = new BooleanValueState()
    result.setSensorIdentifier(sensor.identifier)
    result.setValue(false)
    result.setEmissionDate(emissionDate)

    return result
  }

  def "it emit labels at the initialization by using it's source series" () {
    given: "an entity manager"
    final ApplicationEntityManager entityManager = new ApplicationEntityManager()

    and: "a source sensor"
    final Sensor source = createSourceSensor(entityManager)

    and: "a source series"
    final ZonedDateTime start = ZonedDateTime.parse("2012-10-20T15:20:00.000-05:00")

    entityManager.merge(truthy(source, start))
    entityManager.merge(truthy(source, start.plusMinutes(5)))
    entityManager.merge(truthy(source, start.plusMinutes(7)))
    entityManager.merge(truthy(source, start.plusMinutes(10)))
    entityManager.merge(falsy(source, start.plusMinutes(15)))
    entityManager.merge(falsy(source, start.plusMinutes(30)))
    entityManager.merge(truthy(source, start.plusMinutes(31)))
    entityManager.merge(falsy(source, start.plusMinutes(61)))
    entityManager.merge(truthy(source, start.plusMinutes(63)))
/*
    and: "a labellizer"
    final UpDownToLabelSensorBuilder builder = getUpDownToLabelSensorBuilder(entityManager)
    final UpDownToLabelSensor labellizer = builder.build()

    when: "we initialize the labellizer"
    labellizer.initialize()

    then: "we expect that it has emitted labels in accordance with it's source series"
    def inOrder = Mockito.inOrder(builder.publisher)

    inOrder.verify(builder.publisher).create(Mockito.)
    schemaManager.getHandledSchemaCount() == 5
    schemaManager.hasHandled([
      [
        "class"              : LabelStateCreationSchema.class,
        "start"              : flags[inputSensor, 0].emissionDate,
        "end"                : null,
        "correlations[start]": flags[inputSensor, 0].reference,
        "correlations[end]"  : null,
        "emissionDate"       : flags[inputSensor, 0].emissionDate,
        "tag"                : "presence:living-room",
        "sensor"             : outputSensor
      ],
      [
        "class"            : LabelStateMutationSchema.class,
        "end"              : flags[inputSensor, 4].emissionDate,
        "correlations[end]": flags[inputSensor, 4].reference
      ],
      [
        "class"              : LabelStateCreationSchema.class,
        "start"              : flags[inputSensor, 6].emissionDate,
        "end"                : null,
        "correlations[start]": flags[inputSensor, 6].reference,
        "correlations[end]"  : null,
        "emissionDate"       : flags[inputSensor, 6].emissionDate,
        "tag"                : "presence:living-room",
        "sensor"             : outputSensor
      ],
      [
        "class"            : LabelStateMutationSchema.class,
        "end"              : flags[inputSensor, 7].emissionDate,
        "correlations[end]": flags[inputSensor, 7].reference
      ],
      [
        "class"              : LabelStateCreationSchema.class,
        "start"              : flags[inputSensor, 8].emissionDate,
        "end"                : null,
        "correlations[start]": flags[inputSensor, 8].reference,
        "correlations[end]"  : null,
        "emissionDate"       : flags[inputSensor, 8].emissionDate,
        "tag"                : "presence:living-room",
        "sensor"             : outputSensor
      ]
    ]) == true*/
  }

  /**
   def "it ignore non determinant source sensor emission" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 30}, entityManager)

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
   StateSequenceBuilder.create({with BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(10)})
   andWith BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(15)})
   andWith BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(50)})
   andWith BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(40)})}).build()
   )

   then: "we expect that the underlying handler will ignore them"
   schemaManager.getHandledSchemaCount() == 0}def "it extends an activation if a up flag was discovered before another one" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 30}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit a new up flag before another one"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 0].emissionDate.minusMinutes(10)})}).build()
   )

   then: "we expect that the underlying handler will extand its emitted activation"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"              : LabelStateMutationSchema.class,
   "start"              : emitted[0].emissionDate,
   "correlations[start]": emitted[0].reference,
   "state"              : outputs[outputSensor, 0].reference
   ]
   ]) == true}def "it can also extends infinite activation if a up flag was discovered before another one" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit a new up flag before another one"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 0].emissionDate.minusMinutes(10)})}).build()
   )

   then: "we expect that the underlying handler will extand its emitted activation"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"              : LabelStateMutationSchema.class,
   "start"              : emitted[0].emissionDate,
   "correlations[start]": emitted[0].reference,
   "state"              : outputs[outputSensor, 0].reference
   ]
   ]) == true}def "it truncate an activation if a down flag was discovered in it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 30}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(5)})}).build()
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : emitted[0].emissionDate,
   "correlations[end]": emitted[0].reference,
   "state"            : outputs[outputSensor, 0].reference
   ]
   ]) == true}def "it create an activation if an up flag was discovered before another down flag and outer of any existing activation" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 30
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 40}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 2].emissionDate.plusMinutes(5)})}).build()
   )

   then: "we expect that the underlying handler will extand its emitted activation"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"              : LabelStateCreationSchema.class,
   "start"              : emitted[0].emissionDate,
   "end"                : flags[inputSensor, 4].emissionDate,
   "correlations[start]": emitted[0].reference,
   "correlations[end]"  : flags[inputSensor, 4].reference,
   "tag"                : "presence:living-room",
   "sensor"             : outputSensor
   ]
   ]) == true}def "it create an infinite activation if an up flag was discovered without any down flag after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 30
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 40}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.truthy({withEmittionDate flags[inputSensor, 3].emissionDate.plusMinutes(5)})}).build()
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"              : LabelStateCreationSchema.class,
   "start"              : emitted[0].emissionDate,
   "end"                : null,
   "correlations[start]": emitted[0].reference,
   "correlations[end]"  : null,
   "tag"                : "presence:living-room",
   "sensor"             : outputSensor
   ]
   ]) == true}def "it terminate infinite activation when a down flag was discovered in it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(10)})}).build()
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.getHandledSchemaCount() == 1
   schemaManager.hasHandled([
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : emitted[0].emissionDate,
   "correlations[end]": emitted[0].reference,
   "state"            : outputs[outputSensor, 0].reference
   ]
   ]) == true}def "it split an infinite activation in two if a down flag was discovered between two up flags" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.truthy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit a new up flag before another one"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(10)})}).build()
   )

   then: "we expect that the underlying handler will extand its emitted activation"
   schemaManager.getHandledSchemaCount() == 2
   schemaManager.hasHandled([
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : emitted[0].emissionDate,
   "correlations[end]": emitted[0].reference,
   "state"            : outputs[outputSensor, 0].reference
   ],
   [
   "class"              : LabelStateCreationSchema.class,
   "start"              : flags[inputSensor, 2].emissionDate,
   "end"                : null,
   "correlations[start]": flags[inputSensor, 2].reference,
   "correlations[end]"  : null,
   "tag"                : "presence:living-room",
   "sensor"             : outputSensor
   ]
   ])}def "it split an activation in two if a down flag was discovered between two up flags" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.truthy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we emit a new up flag before another one"
   final List<State> emitted = emit(
   inputSensor, entityManager, runner,
   StateSequenceBuilder.create({with BooleanStateBuilder.falsy({withEmittionDate flags[inputSensor, 0].emissionDate.plusMinutes(10)})}).build()
   )

   then: "we expect that the underlying handler will extand its emitted activation"
   schemaManager.getHandledSchemaCount() == 2
   schemaManager.hasHandled([
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : emitted[0].emissionDate,
   "correlations[end]": emitted[0].reference,
   "state"            : outputs[outputSensor, 0].reference
   ],
   [
   "class"              : LabelStateCreationSchema.class,
   "start"              : flags[inputSensor, 2].emissionDate,
   "end"                : flags[inputSensor, 3].emissionDate,
   "correlations[start]": flags[inputSensor, 2].reference,
   "correlations[end]"  : flags[inputSensor, 3].reference,
   "tag"                : "presence:living-room",
   "sensor"             : outputSensor
   ]
   ])}def "it extends activation if one of its boundary was moved" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 10
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 10
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace some boundary flags"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 2].reference,
   "emissionDate": flags[inputSensor, 2].emissionDate.minusMinutes(10)
   ],
   [
   "state"       : flags[inputSensor, 2].reference,
   "emissionDate": flags[inputSensor, 2].emissionDate
   ],
   [
   "state"       : flags[inputSensor, 4].reference,
   "emissionDate": flags[inputSensor, 4].emissionDate.plusMinutes(10)
   ],
   [
   "state"       : flags[inputSensor, 4].reference,
   "emissionDate": flags[inputSensor, 4].emissionDate
   ],
   ])

   final List<BooleanState> results = mutate(
   inputSensor, entityManager, runner, mutations
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.handledSchemaCount == 4

   schemaManager.hasHandled([
   [
   "class"              : LabelStateMutationSchema.class,
   "start"              : mutations[0].emissionDate,
   "correlations[start]": mutations[0].state,
   "state"              : outputs[outputSensor, 1].reference
   ],
   [
   "class"              : LabelStateMutationSchema.class,
   "start"              : mutations[1].emissionDate,
   "correlations[start]": mutations[1].state,
   "state"              : outputs[outputSensor, 1].reference
   ],
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : mutations[2].emissionDate,
   "correlations[end]": mutations[2].state,
   "state"            : outputs[outputSensor, 1].reference
   ],
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : mutations[3].emissionDate,
   "correlations[end]": mutations[3].state,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ]) == true}def "it act like if a state was created if the mutated state was not an activation boundary" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 10
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 10
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)
   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace some boundary flags"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 3].reference,
   "emissionDate": flags[inputSensor, 3].emissionDate.minusMinutes(30),
   "value"       : false
   ]
   ])

   final List<BooleanState> results = mutate(
   inputSensor, entityManager, runner, mutations
   )

   then: "we expect that the underlying handler will act accordingly"
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it truncate an activation if its start boundary is moved too far away from its original location and if the activation contains another up flag" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 10
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace a boundary flag of an activation state that contains another up flag"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 2].reference,
   "emissionDate": flags[inputSensor, 2].emissionDate.minusMinutes(70),
   "value"       : false
   ]
   ])

   final List<BooleanState> results = mutate(
   inputSensor, entityManager, runner, mutations
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.hasHandled([
   [
   "class"              : LabelStateMutationSchema.class,
   "start"              : flags[inputSensor, 3].emissionDate,
   "correlations[start]": flags[inputSensor, 3].reference,
   "state"              : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it remove an activation if its start boundary is moved too far away from its original location and if the activation does not contains another up flag" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace the last boundary flag of an activation state"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 2].reference,
   "emissionDate": flags[inputSensor, 2].emissionDate.minusMinutes(90),
   "value"       : true
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
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it extand an activation if its end boundary is moved too far away from its original location and if another down flag is present after the old end" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 10
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace the end boundary flag of an activation state after another down flag"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 3].reference,
   "emissionDate": flags[inputSensor, 3].emissionDate.plusMinutes(30),
   "value"       : false
   ]
   ])

   final List<BooleanState> results = mutate(
   inputSensor, entityManager, runner, mutations
   )

   then: "we expect that the underlying handler will act accordingly"
   schemaManager.hasHandled([
   [
   "class"            : LabelStateMutationSchema.class,
   "end"              : flags[inputSensor, 3].emissionDate,
   "correlations[end]": flags[inputSensor, 3].reference,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it merge two activation state if the down flag between them is moved too far away" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we displace the end boundary flag of an activation state after another down flag"
   final List<BooleanStateMutationSchema> mutations = BooleanStateMutationSchema.create([
   [
   "state"       : flags[inputSensor, 3].reference,
   "emissionDate": flags[inputSensor, 3].emissionDate.plusMinutes(80),
   "value"       : false
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
   "class"            : LabelStateMutationSchema.class,
   "end"              : flags[inputSensor, 4].emissionDate,
   "correlations[end]": flags[inputSensor, 4].reference,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it merge two activation state if the down flag between them is changed into an up flag" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"            : LabelStateMutationSchema.class,
   "end"              : flags[inputSensor, 5].emissionDate,
   "correlations[end]": flags[inputSensor, 5].reference,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it extend an activation state if its end flag is changed into an up flag and another down flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"            : LabelStateMutationSchema.class,
   "end"              : flags[inputSensor, 4].emissionDate,
   "correlations[end]": flags[inputSensor, 4].reference,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it truncate an activation state if its start flag is changed into a down flag and another up flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"              : LabelStateMutationSchema.class,
   "start"              : flags[inputSensor, 3].emissionDate,
   "correlations[start]": flags[inputSensor, 3].reference,
   "state"              : outputs[outputSensor, 1].reference
   ]
   ])
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it delete an activation state if its start flag is changed into a down flag and no other up flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   Mockito.verify(runner.getHandler() as UpDownToLabelSensor)
   .inputStateWasCreated(entityManager[mutations[0].state])}def "it ignore no-boundary deletion" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

   runner.initialize()
   schemaManager.reset()

   when: "we remove non-boundary flags"
   for (int index : [3, 4]) {final BooleanState flag = flags[inputSensor, index]
   runner.getHandler().stateWillBeDeleted(
   new StateWillBeDeletedEvent(this, new StateDeletionSchema(flag))
   )
   entityManager.remove(flag)}then: "we expect that the underlying handler will act accordingly"
   schemaManager.handledSchemaCount == 0}def "it merge two activation if the down flag between them is deleted" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"                 : LabelStateMutationSchema.class,
   "end"                   : null,
   "decorrelationsMap[end]": true,
   "state"                 : outputs[outputSensor, 1].reference
   ]
   ])}def "it extend an activation if its end flag is removed and another end flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"            : LabelStateMutationSchema.class,
   "end"              : flags[inputSensor, 3].emissionDate,
   "correlations[end]": flags[inputSensor, 3].reference,
   "state"            : outputs[outputSensor, 1].reference
   ]
   ]) == true}def "it change a finite activation to an infinite activation if its end flag is removed" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"                 : LabelStateMutationSchema.class,
   "end"                   : null,
   "decorrelationsMap[end]": true,
   "state"                 : outputs[outputSensor, 1].reference
   ]
   ]) == true}def "it truncate an activation state if its start flag is removed and another up flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 20
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   "class"              : LabelStateMutationSchema.class,
   "start"              : flags[inputSensor, 2].emissionDate,
   "correlations[start]": flags[inputSensor, 2].reference,
   "state"              : outputs[outputSensor, 1].reference
   ]
   ]) == true}def "it remove an activation state if its start flag is removed and no other up flag exists after it" () {given: "an entity manager and a schemaManager"
   final LocalEntityManager entityManager = new LocalEntityManager()
   final TestSchemaManager schemaManager = new TestSchemaManager()

   and: "an house with some source data"
   final Node house = buildingTestHouseWithSourceSequence({with BooleanStateBuilder.truthy()
   at ZonedDateTime.of(2012, 10, 20, 15, 20, 00, 00, ZoneId.systemDefault())
   andWith BooleanStateBuilder.falsy() afterMinutes 60
   andWith BooleanStateBuilder.truthy() afterMinutes 60
   andWith BooleanStateBuilder.falsy() afterMinutes 20
   andWith BooleanStateBuilder.truthy() afterMinutes 60}, entityManager)

   final ApplicationEntityReference<Sensor> inputSensor = house.getFirstSensorWithName(["living-room", "input"]).get().getReference()
   final ApplicationEntityReference<Node> livingRoom = house.getFirstChildWithName("living-room").get().getReference()

   and: "an initialized runner for the output sensor that tracks the input sensor"
   final VirtualSensorRunner runner = buildRunnerForHouse(house, entityManager, schemaManager)

   final ApplicationEntityReference<Sensor> outputSensor = house.getFirstSensorWithName(["living-room", "output"]).get().getReference()
   final LocalBooleanStateRepository flags = LocalBooleanStateRepository.from(entityManager)
   final LocalLabelRepository outputs = LocalLabelRepository.from(entityManager)

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
   ]) == true}*/
}
