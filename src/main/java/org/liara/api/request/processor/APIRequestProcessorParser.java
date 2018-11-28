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
package org.liara.api.request.processor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.request.APIRequest;
import org.liara.request.APIRequestParameter;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;
import org.liara.request.validator.error.APIRequestParameterValueError;
import org.liara.selection.processor.ProcessorCall;
import org.liara.selection.processor.ProcessorExecutor;
import org.liara.selection.processor.ProcessorParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

public class APIRequestProcessorParser<Result extends Operator>
  implements APIRequestParser<Operator>,
             APIRequestValidator
{
  @NonNull
  private final ProcessorExecutor<Result> _executor;

  @NonNull
  private final ProcessorParser _parser;

  @NonNull
  private final WeakHashMap<@NonNull APIRequest, @NonNull APIRequestValidation> _validations;

  @NonNull
  private final WeakHashMap<@NonNull APIRequest, @NonNull Operator> _results;

  @NonNull
  private final String _field;

  public APIRequestProcessorParser (
    @NonNull final String field, @NonNull final ProcessorExecutor<Result> executor
  )
  {
    _field = field;
    _executor = executor;
    _parser = new ProcessorParser();
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  @NonNull
  @Override
  public Operator parse (@NonNull final APIRequest request) {
    evaluate(request);

    return _results.getOrDefault(request, Identity.INSTANCE);
  }

  @Override
  public @NonNull APIRequestValidation validate (@NonNull final APIRequest request) {
    evaluate(request);
    return _validations.get(request);
  }

  private void evaluate (@NonNull final APIRequest apiRequest) {
    @NonNull APIRequestValidation validation = new APIRequestValidation(apiRequest);

    if (apiRequest.contains(_field)) {
      @NonNull final APIRequestParameter parameter = apiRequest.getParameter(_field);
      @NonNull final List<Operator>      operators = new ArrayList<>();

      for (int index = 0; index < parameter.getSize(); ++index) {
        try {
          @NonNull final ProcessorCall[] calls   = _parser.transpile(parameter.get(index).get());
          @NonNull final Result[]        results = _executor.execute(Arrays.asList(calls));

          if (results.length > 0) {
            operators.add(Composition.of(results));
          }
        } catch (@NonNull final Throwable exception) {
          validation = validation.addError(APIRequestParameterValueError.create(
            parameter,
            index,
            exception.getLocalizedMessage()
          ));
        }
      }

      if (validation.isValid()) {
        _results.put(apiRequest, Composition.of(operators));
      }
    }

    _validations.put(apiRequest, validation);
  }
}
