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
package org.liara.api.controller.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityCollectionView;
import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController
{
  public <Entity, Identifier> ResponseEntity<List<Entity>> indexCollection (
    @NonNull final EntityCollection<Entity, Identifier> collection, 
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    final APIRequest apiRequest = APIRequest.from(request);
    final EntityCollectionView<Entity, Identifier> view = collection.apply(apiRequest);

    if (view.isComplete()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public <Entity, Identifier> long countCollection (
    @NonNull final EntityCollection<Entity, Identifier> collection,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    final EntityCollection<Entity, Identifier> filtered = collection.filter(apiRequest);

    return filtered.getSize();
  }
}
