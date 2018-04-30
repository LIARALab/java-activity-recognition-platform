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

import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.validation.SensorType;
import org.liara.recognition.sensor.VirtualSensorHandler;
import org.liara.recognition.sensor.common.NativeSensor;
import org.springframework.lang.NonNull;

public class OptionalSensorTypeValidator implements ConstraintValidator<SensorType, Optional<String>>
{
  @Override
  public void initialize (@NonNull final SensorType constraintAnnotation) {
  }

  @Override
  public boolean isValid (
    @Nullable final Optional<String> value, 
    @NonNull final ConstraintValidatorContext context
  ) {
    if (value == null || !value.isPresent() || value.get().trim().equals("")) {
      return true;
    } else {
      try {
        final Class<?> type = Class.forName(value.get());
        
        return NativeSensor.class.isAssignableFrom(type) || VirtualSensorHandler.class.isAssignableFrom(type);
      } catch (final ClassNotFoundException exception) {
        return false;
      }
    }
  }
}
