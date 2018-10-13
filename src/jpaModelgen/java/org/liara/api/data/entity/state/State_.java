package org.liara.api.data.entity.state;

import org.liara.api.data.entity.Sensor;

import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.ZonedDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(State.class)
public abstract class State_ extends org.liara.api.data.entity.ApplicationEntity_ {

  public static volatile SingularAttribute<State, ZonedDateTime> emittionDate;
  public static volatile SingularAttribute<State, Boolean>       hasCorrelation;
  public static volatile SingularAttribute<State, Sensor>        sensor;
  public static volatile MapAttribute<State, String, State>      correlations;

}

