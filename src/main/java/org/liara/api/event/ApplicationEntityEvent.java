package org.liara.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Arrays;

public abstract class ApplicationEntityEvent
  extends ApplicationEvent
{
  public ApplicationEntityEvent (@NonNull final Object source) {
    super(source);
  }

  public static class Initialize
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public Initialize (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class Create
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public Create (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class WillCreate
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public WillCreate (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class DidCreate
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public DidCreate (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class Update
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public Update (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class WillUpdate
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public WillUpdate (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class DidUpdate
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public DidUpdate (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class Delete
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public Delete (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class WillDelete
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public WillDelete (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }

  public static class DidDelete
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity[] _entities;

    public DidDelete (@NonNull final Object source, @NonNull final ApplicationEntity... entities) {
      super(source);
      _entities = Arrays.copyOf(entities, entities.length);
    }

    public @NonNull ApplicationEntity[] getEntities () {
      return Arrays.copyOf(_entities, _entities.length);
    }
  }
}
