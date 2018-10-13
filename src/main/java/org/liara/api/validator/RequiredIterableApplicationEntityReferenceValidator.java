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

import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequiredIterableApplicationEntityReferenceValidator 
       implements ConstraintValidator<Required, Iterable<ApplicationEntityReference<?>>>
{
  @Override
  public void initialize (@NonNull final Required constraintAnnotation) { 
    
  }

  @Override
  public boolean isValid (
    @Nullable final Iterable<ApplicationEntityReference<?>> value, 
    @NonNull final ConstraintValidatorContext context
  ) {
    if (value == null) return false;
    
    for (final ApplicationEntityReference<?> reference : value) {
      if (reference == null || reference.isNull()) return false;
    }
    
    return true;
  }
}
