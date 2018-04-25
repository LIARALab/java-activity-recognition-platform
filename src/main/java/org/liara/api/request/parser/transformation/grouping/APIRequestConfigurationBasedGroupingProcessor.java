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
package org.liara.api.request.parser.transformation.grouping;

import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestConfigurationBasedGroupingProcessor<Entity>
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;
  
  @Nullable
  private List<APIRequestGroupingProcessor<Entity>> _processors = null;

  public APIRequestConfigurationBasedGroupingProcessor (
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedGroupingProcessor (
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }
  
  @Override
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  ) {
    if (_processors == null) {
      _processors = _configuration.createGroupingProcessors();
    }
    
    for (APIRequestGroupingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key)) {
        return processor.process(key);
      }
    }
    
    return null;
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key
  ) {
    if (_processors == null) {
      _processors = _configuration.createGroupingProcessors();
    }
    
    for (APIRequestGroupingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key)) {
        return true;
      }
    }
    
    return false;
  }

}
