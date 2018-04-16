package org.liara.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class StringRequiredValidator implements ConstraintValidator<Required, String>
{
  @Override
  public boolean isValid (
    @Nullable final String value,
    @NonNull final ConstraintValidatorContext context
  ) {
    return value != null && value.trim().length() > 0;
  }
}
