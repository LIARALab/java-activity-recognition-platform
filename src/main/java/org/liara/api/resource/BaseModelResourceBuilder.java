package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.RelationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BaseModelResourceBuilder<Model extends ApplicationEntity>
{
  @Nullable
  private Class<Model> _modelClass;

  @Nullable
  private Model _model;

  @Nullable
  private RelationManager _relationManager;

  @Nullable
  private ApplicationContext _context;

  public BaseModelResourceBuilder () {
    _modelClass = null;
    _model = null;
    _relationManager = null;
    _context = null;
  }

  public BaseModelResourceBuilder (@NonNull final BaseModelResourceBuilder<Model> toCopy) {
    _modelClass = toCopy.getModelClass();
    _model = toCopy.getModel();
    _relationManager = toCopy.getRelationManager();
    _context = toCopy.getContext();
  }

  public @NonNull BaseModelResource<Model> build () {
    return new BaseModelResource<>(this);
  }

  public @Nullable Class<Model> getModelClass () {
    return _modelClass;
  }

  public void setModelClass (@Nullable final Class<Model> modelClass) {
    _modelClass = modelClass;
  }

  public @Nullable Model getModel () {
    return _model;
  }

  public void setModel (@Nullable final Model model) {
    _model = model;
  }

  public @Nullable RelationManager getRelationManager () {
    return _relationManager;
  }

  @Autowired
  public void setRelationManager (@Nullable final RelationManager relationManager) {
    _relationManager = relationManager;
  }

  public @Nullable ApplicationContext getContext () {
    return _context;
  }

  @Autowired
  public void setContext (@Nullable final ApplicationContext context) {
    _context = context;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof BaseModelResourceBuilder) {
      @NonNull final BaseModelResourceBuilder otherModelResourceBuilder = (BaseModelResourceBuilder) other;

      return Objects.equals(
        _modelClass,
        otherModelResourceBuilder.getModelClass()
      ) && Objects.equals(
        _model,
        otherModelResourceBuilder.getModel()
      ) && Objects.equals(
        _relationManager,
        otherModelResourceBuilder.getRelationManager()
      ) && Objects.equals(
        _context,
        otherModelResourceBuilder.getContext()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_modelClass, _model, _relationManager, _context);
  }
}
