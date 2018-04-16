package org.liara.api.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.liara.api.collection.EntityCollection;
import org.liara.api.validator.IdentifierOfEntityInCollectionValidator;
import org.liara.api.validator.OptionalIdentifierOfEntityInCollectionValidator;

@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER})
@Documented
@Constraint(validatedBy = { 
  IdentifierOfEntityInCollectionValidator.class,
  OptionalIdentifierOfEntityInCollectionValidator.class
})
public @interface IdentifierOfEntityInCollection
{
  public String message () default "Invalid entity identifier : the given identifier is not attached to any entity of the expected type.";

  public Class<?>[] groups () default {};
  
  public Class<? extends Payload>[] payload () default {};
  
  public Class<? extends EntityCollection<?, Long>> collection ();
}
