package org.liara.api.data.entity.tree;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(NestedSetCoordinates.class)
public abstract class NestedSetCoordinates_ {

	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> directParentSet;
	public static volatile SingularAttribute<NestedSetCoordinates, Integer> depth;
	public static volatile SingularAttribute<NestedSetCoordinates, Integer> size;
	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> hasChildren;
	public static volatile SingularAttribute<NestedSetCoordinates, NestedSetCoordinates> lastChildMarker;
	public static volatile SingularAttribute<NestedSetCoordinates, Integer> start;
	public static volatile SingularAttribute<NestedSetCoordinates, Integer> end;
	public static volatile SingularAttribute<NestedSetCoordinates, NestedSetCoordinates> firstChildMarker;
	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> leaf;
	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> directChildSet;
	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> parentSet;
	public static volatile SingularAttribute<NestedSetCoordinates, Boolean> childSet;

}

