package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.*;
import java.util.stream.Collectors;


public class LocalCorrelationRepository
  extends LocalApplicationEntityRepository<Correlation>
  implements CorrelationRepository
{
  @NonNull
  private final Map<@NonNull ApplicationEntityReference<? extends State>,
                       @NonNull Map<@NonNull ApplicationEntityReference<? extends State>, Correlation>> _correlationsByStart;

  @NonNull
  private final Map<@NonNull ApplicationEntityReference<? extends State>,
                       @NonNull Map<@NonNull ApplicationEntityReference<? extends State>, Correlation>> _correlationsByEnd;

  public LocalCorrelationRepository () {
    super(Correlation.class);
    _correlationsByStart = new HashMap<>();
    _correlationsByEnd = new HashMap<>();
  }

  public LocalCorrelationRepository (@NonNull final LocalApplicationEntityRepository<Correlation> toCopy) {
    super(toCopy);
    _correlationsByStart = new HashMap<>();
    _correlationsByEnd = new HashMap<>();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    @NonNull final Set<@NonNull Correlation> results = new HashSet<>();

    if (_correlationsByStart.containsKey(state)) results.addAll(_correlationsByStart.get(state).values());
    if (_correlationsByEnd.containsKey(state)) results.addAll(_correlationsByEnd.get(state).values());

    @NonNull final List<@NonNull Correlation> correlations = new ArrayList<>(results);
    correlations.sort(ApplicationEntity::orderByIdentifier);

    if (cursor.hasLimit()) {
      return correlations.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
    } else {
      return correlations.subList(cursor.getOffset(), correlations.size());
    }
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatStartBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByStart.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = new ArrayList<>(_correlationsByStart.get(state).values());
    result.sort(ApplicationEntity::orderByIdentifier);

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatStartBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByStart.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = _correlationsByStart.get(state)
                                                                           .values()
                                                                           .stream()
                                                                           .filter(correlation -> Objects.equals(
                                                                             correlation.getName(),
                                                                             name
                                                                           ))
                                                                           .sorted(ApplicationEntity::orderByIdentifier)
                                                                           .collect(Collectors.toList());

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatStartBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByStart.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = _correlationsByStart.get(state)
                                                                           .values()
                                                                           .stream()
                                                                           .filter((@NonNull final Correlation correlation) -> {
                                                                             @NonNull final State startState =
                                                                               getParent()
                                                                                                                 .get(
                                                                                                                   State.class,
                                                                                                                   correlation
                                                                                                                     .getStartStateIdentifier()
                                                                                                                     .getIdentifier()
                                                                                                                 );
                                                                             return Objects.equals(
                                                                               correlation.getName(),
                                                                               name
                                                                             ) && Objects.equals(
                                                                               startState.getSensorIdentifier(),
                                                                               sensor
                                                                             );
                                                                           })
                                                                           .sorted(ApplicationEntity::orderByIdentifier)
                                                                           .collect(Collectors.toList());

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByEnd.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = new ArrayList<>(_correlationsByEnd.get(state).values());
    result.sort(ApplicationEntity::orderByIdentifier);

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatEndsBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByEnd.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = _correlationsByEnd.get(state)
                                                                         .values()
                                                                         .stream()
                                                                         .filter(correlation -> Objects.equals(
                                                                           correlation.getName(),
                                                                           name
                                                                         ))
                                                                         .sorted(ApplicationEntity::orderByIdentifier)
                                                                         .collect(Collectors.toList());

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    if (!_correlationsByEnd.containsKey(state)) {
      return Collections.emptyList();
    }

    @NonNull final List<@NonNull Correlation> result = _correlationsByEnd.get(state)
                                                                         .values()
                                                                         .stream()
                                                                         .filter((@NonNull final Correlation correlation) -> {
                                                                           @NonNull final State startState = getParent()
                                                                                                               .get(
                                                                                                                 State.class,
                                                                                                                 correlation
                                                                                                                   .getStartStateIdentifier()
                                                                                                                   .getIdentifier()
                                                                                                               );
                                                                           return Objects.equals(
                                                                             correlation.getName(),
                                                                             name
                                                                           ) && Objects.equals(
                                                                             startState.getSensorIdentifier(),
                                                                             sensor
                                                                           );
                                                                         })
                                                                         .sorted(ApplicationEntity::orderByIdentifier)
                                                                         .collect(Collectors.toList());

    return cursor.hasLimit() ? result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit())
                             : result.subList(cursor.getOffset(), result.size());
  }

  @Override
  public void onUpdate (
    @Nullable final ApplicationEntity oldEntity, @NonNull final ApplicationEntity newEntity
  )
  {
    super.onUpdate(oldEntity, newEntity);

    if (newEntity instanceof Correlation) {
      @Nullable final Correlation oldCorrelation = (oldEntity == null) ? null : (Correlation) oldEntity;
      @NonNull final Correlation  newCorrelation = (Correlation) newEntity;

      if (oldCorrelation != null) {
        _correlationsByEnd.get(oldCorrelation.getEndStateIdentifier()).remove(oldCorrelation.getStartStateIdentifier());
        if (_correlationsByEnd.get(oldCorrelation.getEndStateIdentifier()).isEmpty()) {
          _correlationsByEnd.remove(oldCorrelation.getEndStateIdentifier());
        }

        _correlationsByStart.get(oldCorrelation.getStartStateIdentifier())
                            .remove(oldCorrelation.getEndStateIdentifier());
        if (_correlationsByStart.get(oldCorrelation.getStartStateIdentifier()).isEmpty()) {
          _correlationsByStart.remove(oldCorrelation.getStartStateIdentifier());
        }
      } else {
        if (!_correlationsByEnd.containsKey(newCorrelation.getEndStateIdentifier())) {
          _correlationsByEnd.put(newCorrelation.getEndStateIdentifier(), new HashMap<>());
        }

        if (!_correlationsByStart.containsKey(newCorrelation.getStartStateIdentifier())) {
          _correlationsByStart.put(newCorrelation.getStartStateIdentifier(), new HashMap<>());
        }
      }

      _correlationsByStart.get(newCorrelation.getStartStateIdentifier())
                          .put(newCorrelation.getEndStateIdentifier(), newCorrelation);
      _correlationsByEnd.get(newCorrelation.getEndStateIdentifier())
                        .put(newCorrelation.getStartStateIdentifier(), newCorrelation);
    }
  }

  @Override
  public void onRemove (
    @NonNull final ApplicationEntity entity
  )
  {
    super.onRemove(entity);

    if (entity instanceof Correlation) {
      @NonNull final Correlation correlation = (Correlation) entity;

      _correlationsByEnd.get(correlation.getEndStateIdentifier()).remove(correlation.getStartStateIdentifier());
      if (_correlationsByEnd.get(correlation.getEndStateIdentifier()).isEmpty()) {
        _correlationsByEnd.remove(correlation.getEndStateIdentifier());
      }

      _correlationsByStart.get(correlation.getStartStateIdentifier()).remove(correlation.getEndStateIdentifier());
      if (_correlationsByStart.get(correlation.getStartStateIdentifier()).isEmpty()) {
        _correlationsByStart.remove(correlation.getStartStateIdentifier());
      }
    }
  }
}
