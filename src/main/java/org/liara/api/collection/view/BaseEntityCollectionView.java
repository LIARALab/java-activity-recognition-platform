package org.liara.api.collection.view;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.lang.NonNull;

public abstract class BaseEntityCollectionView<Entity> implements EntityCollectionView<Entity>
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull
  private final Class<Entity> _entity;
  
  @NonNull
  private List<Entity> _result = null;
  
  public BaseEntityCollectionView (
    @NonNull final EntityManager manager,
    @NonNull final Class<Entity> entity
  ) {
   _manager = manager;
   _entity = entity;
  }
  
  @Override
  public Class<Entity> getEntityType () {
    return _entity;
  }

  @Override
  public List<Entity> get () {
    this.fetch();
    return Collections.unmodifiableList(_result);
  }

  @Override
  public Entity get (final int index) throws IndexOutOfBoundsException {
    this.fetch();
    return _result.get(index);
  }

  @Override
  public EntityManager getManager () {
    return _manager;
  }

  @Override
  public long getSize () {
    this.fetch();
    return _result.size();
  }
  
  /**
   * Fetch the content of the view if the content was not already selected.
   */
  protected void fetch () {
    if (_result == null) {
      _result = _manager.createQuery(createQuery()).getResultList();
    }
  }
}
