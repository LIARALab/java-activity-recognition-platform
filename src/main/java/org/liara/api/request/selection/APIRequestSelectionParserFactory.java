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
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestDefaultValueParser;
import org.liara.request.parser.APIRequestParser;
import org.liara.selection.JPQLSelection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

public final class APIRequestSelectionParserFactory
{
  @NonNull
  private static EntityManager ENTITY_MANAGER;

  @Autowired
  public void registerEntityManager (@NonNull final EntityManager entityManager) {
    ENTITY_MANAGER = entityManager;
  }

  public static APIRequestParser<Operator> withDefault (
    @NonNull final APIRequestParser<Operator> parser, @NonNull final Operator result
  ) {
    return new APIRequestDefaultValueParser<Operator>(result, parser);
  }

  public static @NonNull APIRequestSelectionParser asBoolean (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asBoolean(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asBoolean (
    @NonNull final String parameter, @NonNull final String field
  )
  {
    return null;
  }

  public static @NonNull APIRequestSelectionParser asLong (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asLong(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asLong (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.longTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asShort (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asShort(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asShort (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.shortTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asByte (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asByte(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asByte (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.byteTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asInteger (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asInteger(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asInteger (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.integerTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asDouble (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asDouble(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asDouble (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.doubleTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asFloat (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asFloat(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asFloat (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.floatTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asDateTime (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asDateTime(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asDateTime (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.datetimeTranspiler());
  }

  public static APIRequestSelectionParser datetimeInRange (
    @NonNull final String parameter, @NonNull final String start, @NonNull final String end
  ) {
    @NonNull final Map<@NonNull String, @NonNull String> fields = new HashMap<>();
    fields.put(":this.lower", start);
    fields.put(":this.upper", end);

    return new APIRequestSelectionParser(parameter, fields, JPQLSelection.datetimeTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asDuration (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asDuration(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asDuration (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.durationTranspiler());
  }

  public static @NonNull APIRequestSelectionParser asString (@NonNull final String parameter) {
    return APIRequestSelectionParserFactory.asString(parameter, parameter);
  }

  public static @NonNull APIRequestSelectionParser asString (
    @NonNull final String parameter, @NonNull final String field
  ) {
    return new APIRequestSelectionParser(parameter, field, JPQLSelection.stringTranspiler());
  }

  public static APIRequestJoinSelectionParser asJoinWith (
    @NonNull final String parameter, @NonNull final Class<?> entity
  ) {
    return asJoinWith(parameter, parameter, entity);
  }

  public static APIRequestJoinSelectionParser asJoinWith (
    @NonNull final String parameter, @NonNull final String field, @NonNull final Class<?> entity
  ) {
    return new APIRequestJoinSelectionParser(ENTITY_MANAGER, parameter, field, entity);
  }

  public static APIRequestExistsSelectionParser asCollectionOf (
    @NonNull final String parameter, @NonNull final Class<?> joined, @NonNull final Operator definition
  ) {
    return new APIRequestExistsSelectionParser(ENTITY_MANAGER, parameter, definition, joined
    );
  }
}
