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
package org.liara.api.controller.web;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public final class APIExceptionHandler
{
  @ExceptionHandler(InvalidAPIRequestException.class)
  public ResponseEntity<InvalidAPIRequestException> handleInvalidAPIRequestException (
    @NonNull final InvalidAPIRequestException exception
  )
  {
    exception.printStackTrace();
    return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Void> handleEntityNotFoundException (
    @NonNull final EntityNotFoundException exception
  )
  {
    exception.printStackTrace();
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<Map<String, Object>>> handleException(@NonNull final MethodArgumentNotValidException exception) {
    exception.printStackTrace();

    final List<Map<String, Object>> errors = new ArrayList<>();
    
    exception.getBindingResult()
             .getFieldErrors()
             .stream()
             .map(fieldError -> {
               final Map<String, Object> error = new HashMap<>();

               error.put("object", fieldError.getObjectName());
               error.put("field", fieldError.getField());
               error.put("message", fieldError.getDefaultMessage());
               error.put("rejected", fieldError.getRejectedValue());
               
               return error;
             }).forEach(errors::add);
                                          

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException (@NonNull final Exception exception) {
    exception.printStackTrace();
    return new ResponseEntity<>(exceptionToMap(exception), HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  public Map<String, Object> exceptionToMap(@NonNull final Throwable exception) {
    if (exception == null) return null;
    
    final Map<String, Object> result = new HashMap<>();
    result.put("exception", exception.getClass());
    // result.put("localizedMessage", exception.getLocalizedMessage());
    result.put("message", exception.getMessage());
    
    final List<String> stackTrace = new ArrayList<String>();
    Arrays.asList(exception.getStackTrace()).forEach((StackTraceElement element) -> {
      stackTrace.add(String.join(
        "", 
        "in ", element.getFileName(), 
        " at line ", String.valueOf(element.getLineNumber()),
        " into ", element.getClassName(), "#", element.getMethodName(), 
        element.isNativeMethod() ? " (native method)" : ""
      ));
    });
    
    result.put("stackTrace", stackTrace);
    result.put("cause", exceptionToMap(exception.getCause()));
    
    return result;
  }
}
