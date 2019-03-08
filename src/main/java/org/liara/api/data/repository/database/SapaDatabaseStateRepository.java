package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.SapaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

public class SapaDatabaseStateRepository
{
  @Component
  @Scope("prototype")
  @Primary
  public static class Boolean
    extends DatabaseValueStateRepository<java.lang.Boolean, BooleanValueState>
    implements SapaRepositories.Boolean
  {
    @Autowired
    public Boolean (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        BooleanValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class String
    extends DatabaseValueStateRepository<java.lang.String, StringValueState>
    implements SapaRepositories.String
  {
    @Autowired
    public String (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        StringValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Byte
    extends DatabaseValueStateRepository<java.lang.Byte, ByteValueState>
    implements SapaRepositories.Byte
  {
    @Autowired
    public Byte (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        ByteValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Short
    extends DatabaseValueStateRepository<java.lang.Short, ShortValueState>
    implements SapaRepositories.Short
  {
    @Autowired
    public Short (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        ShortValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Integer
    extends DatabaseValueStateRepository<java.lang.Integer, IntegerValueState>
    implements SapaRepositories.Integer
  {
    @Autowired
    public Integer (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        IntegerValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Long
    extends DatabaseValueStateRepository<java.lang.Long, LongValueState>
    implements SapaRepositories.Long
  {
    @Autowired
    public Long (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        LongValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Float
    extends DatabaseValueStateRepository<java.lang.Float, FloatValueState>
    implements SapaRepositories.Float
  {
    @Autowired
    public Float (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        FloatValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Double
    extends DatabaseValueStateRepository<java.lang.Double, DoubleValueState>
    implements SapaRepositories.Double
  {
    @Autowired
    public Double (@NonNull final EntityManager entityManager) {
      super(
        entityManager,
        DoubleValueState.class
      );
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Numeric
    extends DatabaseValueStateRepository<Number, NumericValueState>
    implements SapaRepositories.Numeric
  {
    @Autowired
    public Numeric (@NonNull final EntityManager entityManager) {
      super(entityManager, NumericValueState.class);
    }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class State
    extends DatabaseStateRepository<org.liara.api.data.entity.state.State>
    implements SapaRepositories.State
  {
    @Autowired
    public State (final @NonNull EntityManager entityManager) {
      super(entityManager, org.liara.api.data.entity.state.State.class);
    }
  }
}
