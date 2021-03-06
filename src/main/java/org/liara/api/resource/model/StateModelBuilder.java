package org.liara.api.resource.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.resource.BaseModelResourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class StateModelBuilder
{
  @NonNull
  private final BaseModelResourceBuilder<State> _baseModelResourceBuilder;

  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private TransactionTemplate _transactionTemplate;

  @Autowired
  public StateModelBuilder(@NonNull final BaseModelResourceBuilder<State> builder) {
    _baseModelResourceBuilder = builder;
    _apiEventPublisher = null;
    _transactionTemplate = null;
  }

  public @Nullable TransactionTemplate getTransactionTemplate () {
    return _transactionTemplate;
  }

  @Autowired
  public void setTransactionTemplate (@Nullable final TransactionTemplate transactionTemplate) {
    _transactionTemplate = transactionTemplate;
  }

  public @NonNull BaseModelResourceBuilder<State> getBaseModelResourceBuilder () {
    return _baseModelResourceBuilder;
  }

  public @Nullable APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  @Autowired
  public void setApiEventPublisher (@Nullable final APIEventPublisher apiEventPublisher) {
    _apiEventPublisher = apiEventPublisher;
  }
}
