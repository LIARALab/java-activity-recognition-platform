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
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.validator.APIRequestFieldValidation;
import org.liara.request.validator.APIRequestFieldValidator;
import org.liara.selection.processor.ProcessorCall;
import org.liara.selection.processor.ProcessorExecutor;
import org.liara.selection.processor.ProcessorParser;

import java.util.Arrays;
import java.util.WeakHashMap;

public class APIRequestProcessorParser<Result extends Operator>
  implements APIRequestFieldParser<Operator>,
             APIRequestFieldValidator
{
  @NonNull
  private final ProcessorExecutor<Result> _executor;

  @NonNull
  private final ProcessorParser _parser;

  @NonNull
  private final WeakHashMap<@NonNull String, @NonNull APIRequestFieldValidation> _validations;

  @NonNull
  private final WeakHashMap<@NonNull String, @NonNull Operator> _results;

  public APIRequestProcessorParser (@NonNull final ProcessorExecutor<Result> executor) {
    _executor = executor;
    _parser = new ProcessorParser();
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  @Override
  public Operator parse (@NonNull final String content) {
    evaluate(content);

    return _results.computeIfAbsent(content, x -> null);
  }

  @Override
  public @NonNull APIRequestFieldValidation validate (@NonNull final String content) {
    evaluate(content);

    return _validations.get(content);
  }

  private void evaluate (@NonNull final String content) {
    @NonNull APIRequestFieldValidation validation = new APIRequestFieldValidation();

    try {
      @NonNull final ProcessorCall[] calls   = _parser.transpile(content);
      @NonNull final Result[]        results = _executor.execute(Arrays.asList(calls));

      if (results.length > 0) {
        _results.put(content, Composition.of(results));
      }
    } catch (@NonNull final Throwable exception) {
      validation.addError(exception.getLocalizedMessage());
    }

    _validations.put(content, validation);
  }
}
