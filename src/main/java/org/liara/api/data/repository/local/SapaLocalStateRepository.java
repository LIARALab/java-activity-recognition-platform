package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.SapaRepositories;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class SapaLocalStateRepository
{
  @Component
  @Scope("prototype")
  public static class Boolean
    extends LocalValueStateRepository<java.lang.Boolean, ValueState.Boolean>
    implements SapaRepositories.Boolean
  {

    public Boolean () { super(ValueState.Boolean.class); }
  }

  @Component
  @Scope("prototype")
  public static class String
    extends LocalValueStateRepository<java.lang.String, ValueState.String>
    implements SapaRepositories.String
  {

    public String () { super(ValueState.String.class); }
  }

  @Component
  @Scope("prototype")
  public static class Byte
    extends LocalValueStateRepository<java.lang.Byte, ValueState.Byte>
    implements SapaRepositories.Byte
  {

    public Byte () { super(ValueState.Byte.class); }
  }

  @Component
  @Scope("prototype")
  public static class Short
    extends LocalValueStateRepository<java.lang.Short, ValueState.Short>
    implements SapaRepositories.Short
  {

    public Short () { super(ValueState.Short.class); }
  }

  @Component
  @Scope("prototype")
  public static class Integer
    extends LocalValueStateRepository<java.lang.Integer, ValueState.Integer>
    implements SapaRepositories.Integer
  {

    public Integer () { super(ValueState.Integer.class); }
  }

  @Component
  @Scope("prototype")
  public static class Long
    extends LocalValueStateRepository<java.lang.Long, ValueState.Long>
    implements SapaRepositories.Long
  {
    public Long () { super(ValueState.Long.class); }
  }

  @Component
  @Scope("prototype")
  public static class Float
    extends LocalValueStateRepository<java.lang.Float, ValueState.Float>
    implements SapaRepositories.Float
  {

    public Float () { super(ValueState.Float.class); }
  }

  @Component
  @Scope("prototype")
  public static class Double
    extends LocalValueStateRepository<java.lang.Double, ValueState.Double>
    implements SapaRepositories.Double
  {

    public Double () { super(ValueState.Double.class); }
  }

  @Component
  @Scope("prototype")
  public static class Numeric
    extends LocalValueStateRepository<Number, ValueState.Numeric>
    implements SapaRepositories.Numeric
  {

    public Numeric () { super(ValueState.Numeric.class); }
  }

  @Component
  @Scope("prototype")
  public static class State
    extends LocalStateRepository<org.liara.api.data.entity.state.State>
    implements SapaRepositories.State
  {
    public State () {
      super(org.liara.api.data.entity.state.State.class);
    }
  }
}
