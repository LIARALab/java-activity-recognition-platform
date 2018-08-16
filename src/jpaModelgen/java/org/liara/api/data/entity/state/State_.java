package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.sensor.Sensor;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(State.class)
public abstract class State_ extends org.liara.api.data.entity.ApplicationEntity_ {

	public static volatile SingularAttribute<State, ZonedDateTime> _emittionDate;
	public static volatile SingularAttribute<State, Sensor> _sensor;
	public static volatile MapAttribute<State, String, State> _correlations;

}

