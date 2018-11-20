/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.validator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.validation.ApplicationEntityReference;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IterableApplicationEntityReferenceValidator
  implements ConstraintValidator<ApplicationEntityReference, Iterable<@Nullable Long>>
{
  @NonNull
  private final EntityManager _entityManager;

  @Nullable
  private Class<? extends ApplicationEntity> _entityType;

  @Autowired 
  public IterableApplicationEntityReferenceValidator(
    @NonNull final EntityManager entityManager
  ) { _entityManager = entityManager; }
  
  @Override
  public void initialize (@NonNull final ApplicationEntityReference constraintAnnotation) {
    _entityType = constraintAnnotation.value();
  }

  @Override
  public boolean isValid (
    @Nullable final Iterable<@Nullable Long> value,
    @NonNull final ConstraintValidatorContext context
  ) {
    if (value == null) return true;

    for (@Nullable final Long identifier : value) {
      if (identifier != null && _entityManager.find(_entityType, identifier) == null) {
        return false;
      }
    }
    
    return true;
  }
}
