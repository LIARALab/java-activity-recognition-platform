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
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.request.APIRequest;
import org.liara.request.APIRequestParameter;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;
import org.liara.request.validator.error.APIRequestParameterValueError;
import org.liara.selection.jpql.JPQLQuery;
import org.liara.selection.jpql.JPQLSelectionTranspiler;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class APIRequestSelectionParser
  implements APIRequestParser<Operator>,
             APIRequestValidator
{
  @NonNull
  private final String _parameter;

  @NonNull
  private final Map<String, String> _fields;

  @NonNull
  private final JPQLSelectionTranspiler _transpiler;

  @NonNull
  private final WeakHashMap<@NonNull APIRequest, @NonNull APIRequestValidation> _validations;

  @NonNull
  private final WeakHashMap<@NonNull APIRequest, @NonNull Operator> _results;

  public APIRequestSelectionParser (
    @NonNull final String parameter, @NonNull final JPQLSelectionTranspiler transpiler
  ) {
    _parameter = parameter;
    _fields = new HashMap<>();
    _fields.put(":this", parameter);
    _transpiler = transpiler;
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  public APIRequestSelectionParser (
    @NonNull final String parameter, @NonNull final String field, @NonNull final JPQLSelectionTranspiler transpiler
  )
  {
    _parameter = parameter;
    _fields = new HashMap<>();
    _fields.put(":this", parameter);
    _transpiler = transpiler;
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  public APIRequestSelectionParser (
    @NonNull final String parameter,
    @NonNull final Map<String, String> fields,
    @NonNull final JPQLSelectionTranspiler transpiler
  )
  {
    _parameter = parameter;
    _fields = new HashMap<>(fields);
    _transpiler = transpiler;
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  @Override
  public @NonNull Operator parse (@NonNull final APIRequest apiRequest) {
    evaluate(apiRequest);
    return _results.getOrDefault(apiRequest, Identity.INSTANCE);
  }

  @Override
  public @NonNull APIRequestValidation validate (@NonNull final APIRequest apiRequest) {
    evaluate(apiRequest);
    return _validations.get(apiRequest);
  }

  private void evaluate (@NonNull final APIRequest apiRequest) {
    @NonNull APIRequestValidation validation = new APIRequestValidation(apiRequest);

    if (apiRequest.contains(_parameter)) {
      @NonNull final APIRequestParameter parameter = apiRequest.getParameter(_parameter);
      @NonNull final Operator[]          operators = new Operator[parameter.getSize()];

      for (int index = 0; index < parameter.getSize(); ++index) {
        try {
          @NonNull final JPQLQuery selection = _transpiler.transpile(parameter.get(index).get());
          @NonNull String          clause    = selection.getClause();

          for (final Map.Entry<@NonNull String, @NonNull String> replacements : _fields.entrySet()) {
            operators[index] = Filter.expression(selection.getClause()
                                                          .replace(replacements.getKey(), replacements.getValue()))
                                     .setParameters(selection.getParameters());
          }
        } catch (@NonNull final Throwable exception) {
          validation = validation.addError(APIRequestParameterValueError.create(
            parameter,
            index,
            exception.getLocalizedMessage()
          ));
        }
      }

      if (validation.isValid()) _results.put(apiRequest, Composition.of(operators));
    }

    _validations.put(apiRequest, validation);
  }
}
