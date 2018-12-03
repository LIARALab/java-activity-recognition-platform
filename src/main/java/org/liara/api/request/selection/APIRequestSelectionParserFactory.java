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
import org.liara.api.collection.RequestConfiguration;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.selection.JPQLSelection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public final class APIRequestSelectionParserFactory
{
  @NonNull
  private EntityManager _entityManager;

  @Autowired
  public APIRequestSelectionParserFactory (@NonNull final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public @NonNull APIRequestFieldParser<Filter> createBoolean (@NonNull final String field) {
    return null;
  }

  public @NonNull APIRequestFieldParser<Filter> createLong (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.longTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createShort (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.shortTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createByte (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.byteTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createInteger (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.integerTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createDouble (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.doubleTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createFloat (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.floatTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createDateTime (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.datetimeTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createDatetimeInRange (
    @NonNull final String start, @NonNull final String end
  ) {
    @NonNull final Map<@NonNull String, @NonNull String> fields = new HashMap<>();
    fields.put(":this.lower", start);
    fields.put(":this.upper", end);

    return new APIRequestSelectionParser(fields, JPQLSelection.datetimeTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createDuration (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.durationTranspiler());
  }

  public @NonNull APIRequestFieldParser<Filter> createString (@NonNull final String field) {
    return new APIRequestSelectionParser(field, JPQLSelection.stringTranspiler());
  }

  public APIRequestJoinSelectionParser createEmbedded (
    @NonNull final RequestConfiguration configuration
  )
  {
    return asEmbedded(parameter, parameter, configuration);
  }

  public APIRequestJoinSelectionParser createEmbedded (
    @NonNull final String parameter,
    @NonNull final String join, @NonNull final RequestConfiguration configuration
  )
  {
    return new APIRequestJoinSelectionParser(parameter, join, configuration);
  }

  public APIRequestJoinSelectionParser createJoinWith (
    @NonNull final String parameter, @NonNull final RequestConfiguration configuration
  ) {
    return asJoinWith(parameter, parameter, configuration);
  }

  public APIRequestJoinSelectionParser createJoinWith (
    @NonNull final String parameter,
    @NonNull final String field, @NonNull final RequestConfiguration configuration
  ) {
    return new APIRequestJoinSelectionParser(parameter, field, configuration);
  }

  public APIRequestExistsSelectionParser createCollectionOf (
    @NonNull final String parameter,
    @NonNull final Operator definition, @NonNull final RequestConfiguration configuration,
    @NonNull final Class<?> type
  )
  {
    return new APIRequestExistsSelectionParser(_entityManager, parameter, definition, configuration, type);
  }
}
