package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LocalValueStateRepository<Value, Wrapper extends ValueState>
  extends LocalStateRepository<Wrapper>
  implements ValueStateRepository<Value, Wrapper>
{
  public LocalValueStateRepository (
    @NonNull final Class<Wrapper> type
  )
  { super(type); }

  @Override
  public @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull Wrapper> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull Wrapper> previous = findPrevious(
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
  public @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull Wrapper> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull Wrapper> nexts = findNext(
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
  public @NonNull List<@NonNull Wrapper> findAllWithValue (
    @NonNull final Collection<Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final List<@NonNull Wrapper> result = new ArrayList<>(cursor.getLimit());
    @NonNull final List<@NonNull Wrapper> all = find(
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
