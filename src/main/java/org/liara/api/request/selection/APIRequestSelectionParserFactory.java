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
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestDefaultValueParser;
import org.liara.request.parser.APIRequestParser;
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

  public APIRequestParser<Operator> withDefault (
    @NonNull final APIRequestParser<Operator> parser, @NonNull final Operator result
  ) {
    return new APIRequestDefaultValueParser<Operator>(result, parser);
  }

  public @NonNull APIRequestSelectionParser asBoolean (@NonNull final String parameter) {
    return asBoolean(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asBoolean (
    @NonNull final String parameter, @NonNull final String field
  )
  {
    return null;
  }

  public @NonNull APIRequestSelectionParser asLong (@NonNull final String parameter) {
    return asLong(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asLong (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.longTranspiler());
  }

  public @NonNull APIRequestSelectionParser asShort (@NonNull final String parameter) {
    return asShort(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asShort (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.shortTranspiler());
  }

  public @NonNull APIRequestSelectionParser asByte (@NonNull final String parameter) {
    return asByte(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asByte (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.byteTranspiler());
  }

  public @NonNull APIRequestSelectionParser asInteger (@NonNull final String parameter) {
    return asInteger(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asInteger (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.integerTranspiler());
  }

  public @NonNull APIRequestSelectionParser asDouble (@NonNull final String parameter) {
    return asDouble(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asDouble (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.doubleTranspiler());
  }

  public @NonNull APIRequestSelectionParser asFloat (@NonNull final String parameter) {
    return asFloat(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asFloat (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.floatTranspiler());
  }

  public @NonNull APIRequestSelectionParser asDateTime (@NonNull final String parameter) {
    return asDateTime(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asDateTime (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.datetimeTranspiler());
  }

  public APIRequestSelectionParser datetimeInRange (
    @NonNull final String parameter, @NonNull final String start, @NonNull final String end
  ) {
    @NonNull final Map<@NonNull String, @NonNull String> fields = new HashMap<>();
    fields.put(":this.lower", start);
    fields.put(":this.upper", end);

    return new APIRequestSelectionParser(parameter, fields, JPQLSelection.datetimeTranspiler());
  }

  public @NonNull APIRequestSelectionParser asDuration (@NonNull final String parameter) {
    return asDuration(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asDuration (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.durationTranspiler());
  }

  public @NonNull APIRequestSelectionParser asString (@NonNull final String parameter) {
    return asString(parameter, parameter);
  }

  public @NonNull APIRequestSelectionParser asString (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.stringTranspiler());
  }

  public APIRequestJoinSelectionParser asEmbedded (
    @NonNull final String parameter, @NonNull final CollectionRequestConfiguration configuration
  )
  {
    return asEmbedded(parameter, parameter, configuration);
  }

  public APIRequestJoinSelectionParser asEmbedded (
    @NonNull final String parameter,
    @NonNull final String join,
    @NonNull final CollectionRequestConfiguration configuration
  )
  {
    return new APIRequestJoinSelectionParser(parameter, join, configuration);
  }

  public APIRequestJoinSelectionParser asJoinWith (
    @NonNull final String parameter, @NonNull final CollectionRequestConfiguration configuration
  ) {
    return asJoinWith(parameter, parameter, configuration);
  }

  public APIRequestJoinSelectionParser asJoinWith (
    @NonNull final String parameter,
    @NonNull final String field,
    @NonNull final CollectionRequestConfiguration configuration
  ) {
    return new APIRequestJoinSelectionParser(parameter, field, configuration);
  }

  public APIRequestExistsSelectionParser asCollectionOf (
    @NonNull final String parameter,
    @NonNull final Operator definition,
    @NonNull final CollectionRequestConfiguration configuration,
    @NonNull final Class<?> type
  )
  {
    return new APIRequestExistsSelectionParser(_entityManager, parameter, definition, configuration, type);
  }
}
