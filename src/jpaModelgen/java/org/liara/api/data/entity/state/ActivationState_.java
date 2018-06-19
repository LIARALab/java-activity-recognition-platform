package org.liara.api.data.entity.state;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.node.Node;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ActivationState.class)
public abstract class ActivationState_ extends org.liara.api.data.entity.state.State_ {

	public static volatile SingularAttribute<ActivationState, State> _endState;
	public static volatile SingularAttribute<ActivationState, State> _startState;
	public static volatile SingularAttribute<ActivationState, Node> _node;

}

