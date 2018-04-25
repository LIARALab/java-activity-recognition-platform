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
package org.liara.api.request.parser.operator;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestConfigurationBasedFilteringOperatorParser<Entity>
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;

  public APIRequestConfigurationBasedFilteringOperatorParser(
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedFilteringOperatorParser(
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    return _configuration.createFilterParser().parse(request);
  }
}
