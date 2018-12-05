package org.liara.api.request.ordering;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.ordering.Order;
import org.liara.selection.processor.Processor;

import java.util.List;

public class APIRequestOrderingProcessor
  implements Processor<Operator>
{
  @NonNull
  private final String _field;

  public APIRequestOrderingProcessor (@NonNull final String field) {
    _field = field;
  }

  @Override
  public @NonNull Order execute (@NonNull final List<@NonNull Object> parameters) {
    if (parameters.size() <= 0) {
      return Order.field(_field).ascending();
    } else if (parameters.get(0).toString().equals("asc")) {
      return Order.field(_field).ascending();
    } else {
      return Order.field(_field).descending();
    }
  }
}
