package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalValueStateRepository<Value>
  extends LocalStateRepository<ValueState<Value>>
  implements ValueStateRepository<Value>
{
  public LocalValueStateRepository (
    @NonNull final Class<ValueState<Value>> type
  )
  { super(type); }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull ValueState<Value>> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull ValueState<Value>> previous = findPrevious(
      date,
      inputSensorIdentifiers,
      Cursor.ALL
    );

    for (int index = 0; index < previous.size() && result.size() < cursor.getLimit(); ++index) {
      if (Objects.equals(previous.get(index).getValue(), value)) {
        result.add(previous.get(index));
      }
    }
    
    return result;
  }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull ValueState<Value>> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull ValueState<Value>> nexts  = findNext(
      date,
      inputSensorIdentifiers,
      Cursor.ALL
    );

    for (int index = 0; index < nexts.size() && result.size() < cursor.getLimit(); ++index) {
      if (nexts.get(index).getValue() == value) {
        result.add(nexts.get(index));
      }
    }
    
    return result;
  }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findAllWithValue (
    @NonNull final List<Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull ValueState<Value>> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull ValueState<Value>> all    = find(
      inputSensorIdentifiers,
      Cursor.ALL
    );

    for (int index = 0; index < all.size() && result.size() < cursor.getLimit(); ++index) {
      if (all.get(index).getValue() == value) {
        result.add(all.get(index));
      }
    }

    return result;
  }
}
