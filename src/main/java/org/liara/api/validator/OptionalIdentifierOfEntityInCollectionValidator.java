package org.liara.api.validator;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.collection.EntityCollection;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

public class OptionalIdentifierOfEntityInCollectionValidator implements ConstraintValidator<IdentifierOfEntityInCollection, Long>
{
  @Autowired
  private ApplicationContext _context;
  
  private Class<? extends EntityCollection<?, Long>> _collection;

  @Override
  public void initialize (@NonNull final IdentifierOfEntityInCollection constraintAnnotation) {
    _collection = constraintAnnotation.collection();
  }

  @Override
  public boolean isValid (
    @Nullable final Long value, 
    @NonNull final ConstraintValidatorContext context
  ) {
    if (value == null) {
      return true;
    } else {
      final EntityCollection<?, Long> collection = _context.getBean(_collection);
      return collection.findById(value) != null;
    }
  }
}
