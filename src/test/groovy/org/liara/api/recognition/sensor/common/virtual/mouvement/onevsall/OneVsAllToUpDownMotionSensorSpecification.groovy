package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall

import java.time.ZonedDateTime

import org.liara.api.data.entity.sensor.Sensor
import org.liara.api.data.entity.node.Node
import org.liara.api.data.entity.state.BooleanState
import org.liara.api.data.entity.tree.LocalNestedSetTree
import org.liara.api.data.repository.local.LocalEntityManager
import org.springframework.lang.NonNull
import spock.lang.Specification

public class OneVsAllToUpDownMotionSensorSpecification
       extends Specification
{
  def Node generateHouse (
    @NonNull final LocalNestedSetTree<Node> tree
  ) {
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
  }
    
  def "it emit boundaries flags from existing sensor data when the virtual sensor is initialized" () {
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
  }
}
