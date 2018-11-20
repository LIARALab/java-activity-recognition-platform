package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.SapaRepositories;
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
    extends DatabaseValueStateRepository<java.lang.Boolean, ValueState.Boolean>
    implements SapaRepositories.Boolean
  {

    public Boolean (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Boolean.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class String
    extends DatabaseValueStateRepository<java.lang.String, ValueState.String>
    implements SapaRepositories.String
  {

    public String (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.String.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Byte
    extends DatabaseValueStateRepository<java.lang.Byte, ValueState.Byte>
    implements SapaRepositories.Byte
  {

    public Byte (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Byte.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Short
    extends DatabaseValueStateRepository<java.lang.Short, ValueState.Short>
    implements SapaRepositories.Short
  {

    public Short (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Short.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Integer
    extends DatabaseValueStateRepository<java.lang.Integer, ValueState.Integer>
    implements SapaRepositories.Integer
  {

    public Integer (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Integer.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Long
    extends DatabaseValueStateRepository<java.lang.Long, ValueState.Long>
    implements SapaRepositories.Long
  {
    public Long (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Long.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Float
    extends DatabaseValueStateRepository<java.lang.Float, ValueState.Float>
    implements SapaRepositories.Float
  {

    public Float (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Float.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Double
    extends DatabaseValueStateRepository<java.lang.Double, ValueState.Double>
    implements SapaRepositories.Double
  {

    public Double (@NonNull final EntityManager entityManager) { super(entityManager, ValueState.Double.class); }
  }

  @Component
  @Scope("prototype")
  @Primary
  public static class Numeric
    extends DatabaseValueStateRepository<Number, ValueState.Numeric>
    implements SapaRepositories.Numeric
  {

    public Numeric (@NonNull final EntityManager entityManager) {
      super(entityManager, ValueState.Numeric.class);
    }
  }
}
