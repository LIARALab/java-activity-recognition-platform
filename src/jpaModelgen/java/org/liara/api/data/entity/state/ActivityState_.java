package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ActivityState.class)
public abstract class ActivityState_ extends org.liara.api.data.entity.state.State_ {

	public static volatile SingularAttribute<ActivityState, ZonedDateTime> _end;
	public static volatile SingularAttribute<ActivityState, ZonedDateTime> _start;
	public static volatile SingularAttribute<ActivityState, String> _tag;

}

