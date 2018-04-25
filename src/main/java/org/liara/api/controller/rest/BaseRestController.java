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
package org.liara.api.controller.rest;

import java.util.List;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.aggregation.EntityAggregationTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.liara.api.collection.view.EntityCollectionAggregation;
import org.liara.api.collection.view.MapView;
import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController
{
  public <Entity> ResponseEntity<List<Entity>> indexCollection (
    @NonNull final EntityCollection<Entity> collection, 
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    final APIRequest apiRequest = APIRequest.from(request);
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefaultConfigurationOf(collection);
    final EntityCollection<Entity> fullCollection = configuration.getOperator(apiRequest).apply(collection);
    final List<Entity> content = configuration.getCursor(apiRequest)
                                              .apply(fullCollection)
                                              .get();
    
    if (content.size() >= fullCollection.getSize()) {
      return new ResponseEntity<>(content, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(content, HttpStatus.PARTIAL_CONTENT);
    }
  }
  
  public <Entity, AggregationType> ResponseEntity<Object> aggregate (
    @NonNull final EntityCollection<Entity> collection,
    @NonNull final HttpServletRequest request,
    @NonNull final EntityAggregationTransformation<Entity, AggregationType> aggregation
  ) throws InvalidAPIRequestException {
    return aggregate(collection, request, aggregation, x -> x);
  }

  public <Entity, AggregationType, Cast> ResponseEntity<Object> aggregate (
    @NonNull final EntityCollection<Entity> collection,
    @NonNull final HttpServletRequest request,
    @NonNull final EntityAggregationTransformation<Entity, AggregationType> aggregation,
    @NonNull final Function<AggregationType, Cast> cast
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefaultConfigurationOf(collection);
    
    final EntityCollection<Entity> filtered = configuration.getOperator(apiRequest).apply(collection);
    final EntityCollectionAggregation<Entity, AggregationType> aggregationResult = aggregation.apply(filtered);
    
    final EntityCollectionGroupTransformation<Entity> groups = configuration.getGrouping(apiRequest);
    
    if (groups == null) {
      return new ResponseEntity<>(
        aggregationResult.get(), 
        HttpStatus.OK
      );
    } else {      
      return new ResponseEntity<>(
        MapView.apply(groups.apply(aggregationResult)).get(), 
        HttpStatus.OK
      );
    }
  }
}
