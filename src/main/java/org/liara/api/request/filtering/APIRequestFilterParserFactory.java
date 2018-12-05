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
package org.liara.api.request.filtering;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.selection.JPQLSelection;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public final class APIRequestFilterParserFactory
{
  public @NonNull APIRequestFilterParser createBoolean (@NonNull final String field) {
    return null;
  }

  public @NonNull APIRequestFilterParser createLong (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.longTranspiler());
  }

  public @NonNull APIRequestFilterParser createShort (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.shortTranspiler());
  }

  public @NonNull APIRequestFilterParser createByte (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.byteTranspiler());
  }

  public @NonNull APIRequestFilterParser createInteger (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.integerTranspiler());
  }

  public @NonNull APIRequestFilterParser createDouble (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.doubleTranspiler());
  }

  public @NonNull APIRequestFilterParser createFloat (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.floatTranspiler());
  }

  public @NonNull APIRequestFilterParser createDateTime (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.datetimeTranspiler());
  }

  public @NonNull APIRequestFilterParser createDatetimeInRange (
    @NonNull final String start, @NonNull final String end
  ) {
    @NonNull final Map<@NonNull String, @NonNull String> fields = new HashMap<>();
    fields.put(":this.lower", start);
    fields.put(":this.upper", end);

    return new APIRequestFilterParser(fields, JPQLSelection.datetimeTranspiler());
  }

  public @NonNull APIRequestFilterParser createDuration (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.durationTranspiler());
  }

  public @NonNull APIRequestFilterParser createString (@NonNull final String field) {
    return new APIRequestFilterParser(field, JPQLSelection.stringTranspiler());
  }
}
