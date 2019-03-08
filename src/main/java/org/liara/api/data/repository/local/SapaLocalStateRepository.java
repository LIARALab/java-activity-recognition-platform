package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.SapaRepositories;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class SapaLocalStateRepository
{
  @Component
  @Scope("prototype")
  public static class Boolean
    extends LocalValueStateRepository<java.lang.Boolean, BooleanValueState>
    implements SapaRepositories.Boolean
  {

    public Boolean () { super(BooleanValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class String
    extends LocalValueStateRepository<java.lang.String, StringValueState>
    implements SapaRepositories.String
  {

    public String () { super(StringValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Byte
    extends LocalValueStateRepository<java.lang.Byte, ByteValueState>
    implements SapaRepositories.Byte
  {

    public Byte () { super(ByteValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Short
    extends LocalValueStateRepository<java.lang.Short, ShortValueState>
    implements SapaRepositories.Short
  {

    public Short () { super(ShortValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Integer
    extends LocalValueStateRepository<java.lang.Integer, IntegerValueState>
    implements SapaRepositories.Integer
  {

    public Integer () { super(IntegerValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Long
    extends LocalValueStateRepository<java.lang.Long, LongValueState>
    implements SapaRepositories.Long
  {
    public Long () { super(LongValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Float
    extends LocalValueStateRepository<java.lang.Float, FloatValueState>
    implements SapaRepositories.Float
  {

    public Float () { super(FloatValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Double
    extends LocalValueStateRepository<java.lang.Double, DoubleValueState>
    implements SapaRepositories.Double
  {

    public Double () { super(DoubleValueState.class); }
  }

  @Component
  @Scope("prototype")
  public static class Numeric
    extends LocalValueStateRepository<Number, NumericValueState>
    implements SapaRepositories.Numeric
  {

    public Numeric () { super(NumericValueState.class); }
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
