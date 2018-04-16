package org.liara.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class RequiredValidator implements ConstraintValidator<Required, Object>
{
  @Override
  public boolean isValid (
    @Nullable final Object value,
    @NonNull final ConstraintValidatorContext context
  ) {
    return value != null;
  }
}
