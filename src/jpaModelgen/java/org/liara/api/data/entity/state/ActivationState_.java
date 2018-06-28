package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.liara.api.data.entity.node.Node;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ActivationState.class)
public abstract class ActivationState_ extends org.liara.api.data.entity.state.State_ {

	public static volatile SingularAttribute<ActivationState, ZonedDateTime> _end;
	public static volatile SingularAttribute<ActivationState, ZonedDateTime> _start;
	public static volatile SingularAttribute<ActivationState, Node> _node;

}

