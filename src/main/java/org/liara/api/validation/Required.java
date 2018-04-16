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

import org.liara.api.validator.OptionalRequiredValidator;
import org.liara.api.validator.OptionalStringRequiredValidator;
import org.liara.api.validator.RequiredValidator;
import org.liara.api.validator.StringRequiredValidator;

@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER})
@Documented
@Constraint(validatedBy = { 
  StringRequiredValidator.class,
  OptionalStringRequiredValidator.class,
  RequiredValidator.class,
  OptionalRequiredValidator.class
})
public @interface Required
{
  public String message () default "The required field is empty.";

  public Class<?>[] groups () default {};
  
  public Class<? extends Payload>[] payload () default {};
}
