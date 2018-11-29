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
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.validator.APIRequestFieldValidation;
import org.liara.request.validator.APIRequestFieldValidator;
import org.liara.selection.TranspilationException;
import org.liara.selection.jpql.JPQLQuery;
import org.liara.selection.jpql.JPQLSelectionTranspiler;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class APIRequestSelectionParser
  implements APIRequestFieldParser<Operator>,
             APIRequestFieldValidator
{
  @NonNull
  private final Map<String, String> _fields;

  @NonNull
  private final JPQLSelectionTranspiler _transpiler;

  @NonNull
  private final WeakHashMap<@NonNull String, @NonNull APIRequestFieldValidation> _validations;

  @NonNull
  private final WeakHashMap<@NonNull String, @NonNull Operator> _results;

  public APIRequestSelectionParser (
    @NonNull final String field, @NonNull final JPQLSelectionTranspiler transpiler
  )
  {
    _fields = new HashMap<>();
    _fields.put(":this", field);
    _transpiler = transpiler;
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  public APIRequestSelectionParser (
    @NonNull final Map<String, String> fields,
    @NonNull final JPQLSelectionTranspiler transpiler
  )
  {
    _fields = new HashMap<>(fields);
    _transpiler = transpiler;
    _validations = new WeakHashMap<>();
    _results = new WeakHashMap<>();
  }

  @Override
  public @NonNull Operator parse (@NonNull final String field) {
    evaluate(field);
    return _results.getOrDefault(field, Identity.INSTANCE);
  }

  @Override
  public @NonNull APIRequestFieldValidation validate (@NonNull final String field) {
    evaluate(field);
    return _validations.get(field);
  }

  private void evaluate (@NonNull final String field) {
    if (_validations.containsKey(field)) return;

    @NonNull final APIRequestFieldValidation validation = new APIRequestFieldValidation();

    try {
      @NonNull final JPQLQuery selection = _transpiler.tryToTranspile(field);
      _results.put(field,
        Filter.expression(replace(selection.getClause(), _fields)).setParameters(selection.getParameters())
      );
    } catch (@NonNull final TranspilationException exception) {
      validation.addError(
        "Line : " + exception.getLine() + ", character : " + exception.getCharacter() + ", " + exception.getMessage() +
        ", near : '" + near(field, exception.getCharacter(), 10) +
        "', please look at this parameter filtering documentation for more information about this error.");
    } catch (@NonNull final Throwable exception) {
      validation.addError(exception.getLocalizedMessage());
    }

    _validations.put(field, validation);
  }

  private @NonNull String replace (
    @NonNull final String clause, @NonNull final Map<@NonNull String, @NonNull String> fields
  )
  {
    @NonNull String result = clause;

    for (final Map.@NonNull Entry<@NonNull String, @NonNull String> field : fields.entrySet()) {
      result = result.replace(field.getKey(), field.getValue());
    }

    return result;
  }

  private @NonNull String near (
    @NonNull final String sequence, final int character, final int characters
  )
  {
    final int start = Math.max(0, character - characters);
    final int end   = Math.min(sequence.length(), character + characters);

    return (start != 0 ? "..." : "") + sequence.substring(start, end) + (end != sequence.length() ? "..." : "");
  }
}
