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
package org.liara.api.request.ordering;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.EntityBasedOrderingConfiguration;
import org.liara.api.collection.RequestConfiguration;
import org.liara.collection.operator.ordering.Order;
import org.liara.selection.processor.ProcessorCall;
import org.liara.selection.processor.ProcessorExecutor;

import java.util.ArrayList;
import java.util.List;

public class APIRequestJoinOrderingExecutor
  implements ProcessorExecutor<Order>
{
  @NonNull
  private final String _parameter;

  @NonNull
  private final RequestConfiguration _configuration;

  public APIRequestJoinOrderingExecutor (
    @NonNull final String parameter, @NonNull final RequestConfiguration configuration
  ) {
    _parameter = parameter;
    _configuration = configuration;
  }

  @Override
  public @NonNull Order[] execute (@NonNull final Iterable<@NonNull ProcessorCall> calls) {
    @NonNull final List<ProcessorCall> subCalls = new ArrayList<>();

    for (@NonNull final ProcessorCall call : calls) {
      if (call.getIdentifier(0).equals(_parameter)) {
        subCalls.add(call.next());
      }
    }

    if (subCalls.size() > 0 && _configuration instanceof EntityBasedOrderingConfiguration) {
      return (Order[]) ((EntityBasedOrderingConfiguration) _configuration).getExecutor().execute(subCalls);
    }

    return new Order[0];
  }
}
