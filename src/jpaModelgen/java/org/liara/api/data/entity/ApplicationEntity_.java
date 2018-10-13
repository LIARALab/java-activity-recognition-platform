package org.liara.api.data.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.ZonedDateTime;
import java.util.UUID;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ApplicationEntity.class)
public abstract class ApplicationEntity_ {

  public static volatile SingularAttribute<ApplicationEntity, Long>          identifier;
  public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> updateDate;
  public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> deletionDate;
  public static volatile SingularAttribute<ApplicationEntity, UUID>          UUID;
  public static volatile SingularAttribute<ApplicationEntity, ZonedDateTime> creationDate;

}

