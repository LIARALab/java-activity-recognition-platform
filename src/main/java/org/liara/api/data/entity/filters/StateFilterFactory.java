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
package org.liara.api.data.entity.filters;

import java.util.Arrays;
import java.util.List;

import org.liara.api.data.entity.State;
import org.liara.api.request.parser.APIRequestEntityCollectionFilterParser;
import org.liara.api.request.parser.APIRequestEntityFieldFilterParserFactory;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;

public final class StateFilterFactory implements EntityFilterFactory<State>
{
  @Override
  public APIRequestEntityCollectionFilterParser<State> createFilterParser () {
    return new APIRequestEntityCollectionFilterParser<>(Arrays.asList(
      APIRequestEntityFieldFilterParserFactory.integer("identifier", (builder, query, root) -> root.get("_identifier")),
      APIRequestEntityFieldFilterParserFactory.datetime("creationDate", (builder, query, root) -> root.get("_creationDate")),
      APIRequestEntityFieldFilterParserFactory.datetime("deletionDate", (builder, query, root) -> root.get("_deletionDate")),
      APIRequestEntityFieldFilterParserFactory.datetime("updateDate", (builder, query, root) -> root.get("_updateDate")),
      APIRequestEntityFieldFilterParserFactory.datetime("date", (builder, query, root) -> root.get("_date"))   
    ));
  }

  @Override
  public List<APIRequestValidator> createValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("date")
    );
  }
}
