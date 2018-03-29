/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.domus.api.controller.web;

import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

import org.domus.api.APIErrorMessage;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.request.validator.error.InvalidAPIRequestException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public final class APIExceptionHandler
{
  @ExceptionHandler(InvalidAPIRequestException.class)
  public ResponseEntity<InvalidAPIRequestException> handleInvalidAPIRequestException (
    @NonNull final InvalidAPIRequestException exception
  )
  {
    return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<APIErrorMessage> handleEntityNotFoundException (
    @NonNull final EntityNotFoundException exception
  )
  {
    return new ResponseEntity<>(new APIErrorMessage("The requested entity does not exists."), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Exception> handleException (@NonNull final Exception exception) {
    return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
