package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class WritingSession
  implements Loggable
{
  @NonNull
  private final EntityManagerFactory _entityManagerFactory;

  @NonNull
  private final EntityManager _sharedEntityManager;

  @Nullable
  private EntityManager _currentEntityManager;

  @Autowired
  public WritingSession (
    @NonNull final EntityManagerFactory entityManagerFactory,
    @NonNull final EntityManager entityManager
  ) {
    _entityManagerFactory = entityManagerFactory;
    _sharedEntityManager = entityManager;
    _currentEntityManager = null;
  }

  public boolean isActive () {
    return _currentEntityManager != null;
  }

  public void begin () {
    /*if (_currentEntityManager == null) {
      getLogger().info("WRITING SESSION STARTED");
      _currentEntityManager = _entityManagerFactory.createEntityManager();
      _currentEntityManager.getTransaction().begin();
    } else {
      throw new Error("Unable to begin an already active writing session.");
    }*/
  }

  public void abort (@NonNull final Throwable throwable) {
    /*if (_currentEntityManager != null) {
      getLogger().info("WRITING SESSION ABORTED");
      _currentEntityManager.flush();
      _currentEntityManager.getTransaction().rollback();
      _currentEntityManager.close();
      _currentEntityManager = null;

      if (getLogger().getLevel() == null || getLogger().getLevel().intValue() <= Level.FINE
      .intValue()) {
        throwable.printStackTrace();
      }
      getLogger().throwing(getClass().getName(), "abort", throwable);

      throw new Error("Writing session aborted.", throwable);
    } else {
      throw new Error("Unable to abort an inactive writing session.");
    }*/
  }

  public void finish () {
    /*if (_currentEntityManager != null) {
      getLogger().info("WRITING SESSION ENDED");
      _currentEntityManager.flush();
      _currentEntityManager.getTransaction().commit();
      _currentEntityManager.close();
      _currentEntityManager = null;
    } else {
      throw new Error("Unable to finish an inactive writing session.");
    }*/
  }

  public @NonNull EntityManager getEntityManager () {
    /*if (_currentEntityManager == null) {
      throw new Error("Unable to get an entity manager from an inactive writing session.");
    } else {*/
    return _sharedEntityManager;
    //}
  }

  public @NonNull EntityManagerFactory getEntityManagerFactory () {
    return _entityManagerFactory;
  }
}
