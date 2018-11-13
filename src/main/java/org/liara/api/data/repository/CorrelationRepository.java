package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.List;
import java.util.Optional;

public interface CorrelationRepository
  extends ApplicationEntityRepository<Correlation>
{
  default @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    return findCorrelationsOf(state, Cursor.ALL);
  }

  @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationThatSartBy (
    @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsThatStartBy(state, Cursor.FIRST);
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsThatStartBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationWithNameAndThatStartBy (
    @NonNull final String name, @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsWithNameAndThatStartBy(
      name,
      state,
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatStartBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationFromSeriesWithNameAndThatStartBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsFromSeriesWithNameAndThatStartBy(
      sensor,
      name,
      state,
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatStartBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsThatEndsBy(state, Cursor.FIRST);
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationWithNameAndThatEndsBy (
    @NonNull final String name, @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsWithNameAndThatEndsBy(name, state, Cursor.FIRST);
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatEndsBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationFromSeriesWithNameAndThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsFromSeriesWithNameAndThatEndsBy(
      sensor,
      name,
      state,
      Cursor.FIRST
    );
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  );
}
