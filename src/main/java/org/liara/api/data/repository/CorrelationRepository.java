package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.Correlation;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.List;
import java.util.Optional;

public interface CorrelationRepository
  extends ApplicationEntityRepository<Correlation>
{
  default @NonNull List<@NonNull Correlation> findCorrelationsOf (@NonNull final Long stateIdentifier) {
    return findCorrelationsOf(
      stateIdentifier,
      Cursor.ALL
    );
  }

  @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final Long stateIdentifier, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationThatSartBy (
    @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsThatStartBy(
      stateIdentifier,
      Cursor.FIRST
    );
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsThatStartBy (
    @NonNull final Long stateIdentifier, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationWithNameAndThatStartBy (
    @NonNull final String name, @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsWithNameAndThatStartBy(
      name,
      stateIdentifier,
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatStartBy (
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationFromSeriesWithNameAndThatStartBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name, @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsFromSeriesWithNameAndThatStartBy(
      sensorIdentifier,
      name,
      stateIdentifier,
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatStartBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationThatEndsBy (
    @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsThatEndsBy(
      stateIdentifier,
      Cursor.FIRST
    );
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsThatEndsBy (
    @NonNull final Long stateIdentifier, @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationWithNameAndThatEndsBy (
    @NonNull final String name, @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsWithNameAndThatEndsBy(
      name,
      stateIdentifier,
      Cursor.FIRST
    );
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatEndsBy (
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Correlation> findFirstCorrelationFromSeriesWithNameAndThatEndsBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name,
    @NonNull final Long stateIdentifier
  )
  {
    @NonNull final List<@NonNull Correlation> result = findCorrelationsFromSeriesWithNameAndThatEndsBy(
      sensorIdentifier,
      name,
      stateIdentifier,
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatEndsBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  );
}
