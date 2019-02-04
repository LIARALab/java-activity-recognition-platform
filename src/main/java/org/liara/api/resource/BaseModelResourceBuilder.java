package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.RelationManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class BaseModelResourceBuilder<Model extends ApplicationEntity>
{
  @Nullable
  private Class<Model> _modelClass;

  @Nullable
  private Model _model;

  @Nullable
  private RelationManager _relationManager;

  @Nullable
  private CollectionResourceBuilder _collectionResourceBuilder;

  public BaseModelResourceBuilder () {
    _modelClass = null;
    _model = null;
    _relationManager = null;
    _collectionResourceBuilder = null;
  }

  public @NonNull BaseModelResource<Model> build () {
    return new BaseModelResource<>(this);
  }

  public @Nullable CollectionResourceBuilder getCollectionResourceBuilder () {
    return _collectionResourceBuilder;
  }

  public void setCollectionResourceBuilder (
    @Nullable final CollectionResourceBuilder collectionResourceBuilder
  ) {
    _collectionResourceBuilder = collectionResourceBuilder;
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

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof BaseModelResourceBuilder) {
      @NonNull
      final BaseModelResourceBuilder otherModelResourceBuilder = (BaseModelResourceBuilder) other;

      return Objects.equals(_modelClass, otherModelResourceBuilder.getModelClass()) &&
             Objects.equals(_model, otherModelResourceBuilder.getModel()) &&
             Objects.equals(
               _relationManager,
               otherModelResourceBuilder.getRelationManager()
             ) &&
             Objects.equals(
               _collectionResourceBuilder,
               otherModelResourceBuilder.getCollectionResourceBuilder()
             );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_modelClass, _model, _relationManager, _collectionResourceBuilder);
  }
}
