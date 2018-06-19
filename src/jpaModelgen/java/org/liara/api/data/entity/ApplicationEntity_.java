package org.liara.api.data.entity;

import java.time.ZonedDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ApplicationEntity.class)
public abstract class ApplicationEntity_ {

	public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> _deletionDate;
	public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> _updateDate;
	public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> _creationDate;
	public static volatile SingularAttribute<ApplicationEntity, Long> _identifier;

}

