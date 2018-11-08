package org.liara.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.context.ApplicationEvent;

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
    private final ApplicationEntity _applicationEntity;

    public Initialize (@NonNull final Object source, @NonNull final ApplicationEntity applicationEntity) {
      super(source);
      _applicationEntity = applicationEntity;
    }

    public @NonNull ApplicationEntity getApplicationEntity () {
      return _applicationEntity;
    }
  }

  public static class Create
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity _applicationEntity;

    public Create (@NonNull final Object source, @NonNull final ApplicationEntity applicationEntity) {
      super(source);
      _applicationEntity = applicationEntity;
    }

    public @NonNull ApplicationEntity getApplicationEntity () {
      return _applicationEntity;
    }
  }

  public static class Update
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity _applicationEntity;

    public Update (@NonNull final Object source, @NonNull final ApplicationEntity applicationEntity) {
      super(source);
      _applicationEntity = applicationEntity;
    }

    public @NonNull ApplicationEntity getApplicationEntity () {
      return _applicationEntity;
    }
  }

  public static class Delete
    extends ApplicationEntityEvent
  {
    @NonNull
    private final ApplicationEntity _applicationEntity;

    public Delete (@NonNull final Object source, @NonNull final ApplicationEntity applicationEntity) {
      super(source);
      _applicationEntity = applicationEntity;
    }

    public @NonNull ApplicationEntity getApplicationEntity () {
      return _applicationEntity;
    }
  }
}
