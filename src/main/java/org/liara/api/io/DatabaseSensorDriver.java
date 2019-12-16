package org.liara.api.io;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.AnyStateRepository;
import org.liara.api.event.entity.*;
import org.liara.api.event.sensor.DidCreateSensorEvent;
import org.liara.api.event.sensor.DidDeleteSensorEvent;
import org.liara.api.event.sensor.WillCreateSensorEvent;
import org.liara.api.event.sensor.WillDeleteSensorEvent;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseSensorDriver
{
  @NonNull
  private final ApplicationEventPublisher _publisher;

  @NonNull
  private final AnyStateRepository _anyStateRepository;

  @Autowired
  public DatabaseSensorDriver (
    @NonNull final ApplicationEventPublisher publisher,
    @NonNull final AnyStateRepository anyStateRepository
  ) {
    _publisher = publisher;
    _anyStateRepository = anyStateRepository;
  }

  @EventListener
  public void willDelete (@NonNull final WillDeleteSensorEvent deletion) {
    Objects.requireNonNull(deletion.getSensor().getIdentifier());

    @NonNull final List<@NonNull Long> sensors = (
      Collections.singletonList(deletion.getSensor().getIdentifier())
    );
    @NonNegative final int size  = _anyStateRepository.count(sensors).intValue();
    @NonNegative final int pages = (size / 1000) + (size % 1000 == 0 ? 0 : 1);

    for (int index = 0; index < pages; ++index) {
      @NonNull final List<@NonNull State> state = _anyStateRepository.find(
        sensors, new Cursor(index * 1000, 1000)
      );

      _publisher.publishEvent(
        new DeleteApplicationEntityEvent(this, state.toArray(new ApplicationEntity[0]))
      );
    }
  }

  @EventListener
  public void willDeleteApplicationEntity (
    @NonNull final WillDeleteApplicationEntityEvent deletion
  ) {
    if (deletion.getEntity() instanceof Sensor) {
      _publisher.publishEvent(new WillDeleteSensorEvent(this, (Sensor) deletion.getEntity()));
    }
  }

  @EventListener
  public void didDeleteApplicationEntity (
    @NonNull final DidDeleteApplicationEntityEvent deletion
  ) {
    if (deletion.getEntity() instanceof Sensor) {
      _publisher.publishEvent(new DidDeleteSensorEvent(this, (Sensor) deletion.getEntity()));
    }
  }

  @EventListener
  public void willCreate (@NonNull final WillCreateApplicationEntityEvent creation) {
    if (creation.getEntity() instanceof Sensor) {
      _publisher.publishEvent(new WillCreateSensorEvent(this, (Sensor) creation.getEntity()));
    }
  }

  @EventListener
  public void didCreate (@NonNull final DidCreateApplicationEntityEvent creation) {
    if (creation.getEntity() instanceof Sensor) {
      _publisher.publishEvent(new DidCreateSensorEvent(this, (Sensor) creation.getEntity()));
    }
  }
}
