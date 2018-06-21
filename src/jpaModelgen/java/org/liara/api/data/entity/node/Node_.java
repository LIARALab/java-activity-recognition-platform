package org.liara.api.data.entity.node;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.tree.NestedSetCoordinates;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Node.class)
public abstract class Node_ extends org.liara.api.data.entity.ApplicationEntity_ {

	public static volatile SetAttribute<Node, Sensor> _sensors;
	public static volatile SingularAttribute<Node, NestedSetCoordinates> _coordinates;
	public static volatile SingularAttribute<Node, String> _name;
	public static volatile SetAttribute<Node, ActivationState> _presences;
	public static volatile SingularAttribute<Node, String> _type;

}

