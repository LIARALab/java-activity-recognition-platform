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
package org.liara.api.resource.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.resource.BaseModelResource;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SensorModel
  extends BaseModelResource<Sensor>
{
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final TransactionTemplate _transactionTemplate;

  @Autowired
  public SensorModel (@NonNull final SensorModelBuilder builder) {
    super(Objects.requireNonNull(builder.getBaseModelResourceBuilder()));
    _apiEventPublisher = Objects.requireNonNull(builder.getApiEventPublisher());
    _transactionTemplate = Objects.requireNonNull(builder.getTransactionTemplate());
  }

  @Override
  public @NonNull Mono<RestResponse> delete (@NonNull final RestRequest request)
  throws UnsupportedOperationException, IllegalRestRequestException {
    _transactionTemplate.execute(status -> this.tryToDelete());

    return Mono.just(RestResponse.ofType(String.class).empty());
  }

  private boolean tryToDelete () {
    try {
      _apiEventPublisher.delete(getModel());
      return true;
    } catch (@NonNull final Throwable throwable) {
      throw new Error(
        "Unable to delete sensor " + getModel().getIdentifier() + ".",
        throwable
      );
    }
  }
}
