package org.liara.api.data.entity.node;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Node.class)
public abstract class Node_ extends org.liara.api.data.entity.ApplicationEntity_ {

	public static volatile SetAttribute<Node, Sensor> _sensors;
	public static volatile SingularAttribute<Node, String> _name;
	public static volatile SingularAttribute<Node, Integer> _setStart;
	public static volatile ListAttribute<Node, ActivationState> _presences;
	public static volatile SingularAttribute<Node, String> _type;
	public static volatile SingularAttribute<Node, Integer> _depth;
	public static volatile SingularAttribute<Node, Integer> _setEnd;

}

