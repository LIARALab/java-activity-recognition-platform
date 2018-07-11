package org.liara.api.data.entity.sensor;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.state.State;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Sensor.class)
public abstract class Sensor_ extends org.liara.api.data.entity.ApplicationEntity_ {

	public static volatile SingularAttribute<Sensor, String> _unit;
	public static volatile SingularAttribute<Sensor, String> _name;
	public static volatile SingularAttribute<Sensor, Boolean> _virtual;
	public static volatile SingularAttribute<Sensor, String> _type;
	public static volatile SetAttribute<Sensor, State> _states;
	public static volatile SingularAttribute<Sensor, Node> _node;

}

