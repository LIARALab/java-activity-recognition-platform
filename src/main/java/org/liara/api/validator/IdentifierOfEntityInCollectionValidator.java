package org.liara.api.validator;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.collection.EntityCollection;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

public class IdentifierOfEntityInCollectionValidator implements ConstraintValidator<IdentifierOfEntityInCollection, Optional<Long>>
{
  @Autowired
  private ApplicationContext _context;
  
  private Class<? extends EntityCollection<?>> _collection;

  @Override
  public void initialize (@NonNull final IdentifierOfEntityInCollection constraintAnnotation) {
    _collection = constraintAnnotation.collection();
  }

  @Override
  public boolean isValid (
    @Nullable final Optional<Long> value, 
    @NonNull final ConstraintValidatorContext context
  ) {
    if (value.isPresent()) {
      final EntityCollection<?> collection = _context.getBean(_collection);
      return collection.findByIdentifier(value.get()).isPresent();
    } else {
      return true;
    }
  }
}
