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
package org.liara.api.request.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionRequestConfiguration;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.joining.Join;
import org.liara.request.APIRequest;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;

public class APIRequestJoinSelectionParser
  implements APIRequestParser<Operator>,
             APIRequestValidator
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final String _join;
  
  @NonNull
  private final CollectionRequestConfiguration _configuration;

  public APIRequestJoinSelectionParser (
    @NonNull final String field, @NonNull final String join, @NonNull final CollectionRequestConfiguration configuration
  ) {
    _field = field;
    _join = join;
    _configuration = configuration;
  }

  @Override
  public @NonNull Operator parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.getRequest(_field);

    if (subRequest.getSize() > 0) {
      return new Join(_join).apply(_configuration.parse(request));
    } else {
      return Identity.INSTANCE;
    }
  }

  @Override
  public @NonNull APIRequestValidation validate (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.getRequest(_field);

    return (subRequest.getSize() > 0) ? _configuration.validate(subRequest) : new APIRequestValidation(subRequest);
  }
}
