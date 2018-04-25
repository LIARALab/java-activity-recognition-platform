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
package org.liara.api.request.parser.operator.ordering;

import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator.Direction;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestConfigurationBasedOrderingProcessor<Entity>
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;
  
  @Nullable
  private List<APIRequestOrderingProcessor<Entity>> _processors = null;

  public APIRequestConfigurationBasedOrderingProcessor (
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedOrderingProcessor (
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }
  
  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final Direction direction
  ) {
    if (_processors == null) {
      _processors = _configuration.createOrderingProcessors();
    }
    
    for (APIRequestOrderingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key, direction)) {
        return processor.process(key, direction);
      }
    }
    
    return new EntityCollectionIdentityOperator<>();
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key, 
    @NonNull final Direction direction
  ) {
    if (_processors == null) {
      _processors = _configuration.createOrderingProcessors();
    }
    
    for (APIRequestOrderingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key, direction)) {
        return true;
      }
    }
    
    return false;
  }

}
