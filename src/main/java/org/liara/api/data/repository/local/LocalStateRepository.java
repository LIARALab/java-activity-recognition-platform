package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.StateRepository;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LocalStateRepository<TimeState extends State>
       extends LocalApplicationEntityRepository<TimeState>
  implements StateRepository<TimeState>
{
  @NonNull
  private final Map<@NonNull Long, @NonNull TreeSet<@NonNull Entry<TimeState>>> _statesBySensors;

  public LocalStateRepository (@NonNull final Class<TimeState> type) {
    super(type);
    _statesBySensors = new HashMap<>();
  }

  public LocalStateRepository (@NonNull final LocalStateRepository<TimeState> toCopy) {
    super(toCopy);
    _statesBySensors = new HashMap<>();
  }

  @Override
  public @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TreeSet<@NonNull Entry<TimeState>> union = getStatesOf(sensorIdentifiers);


    if (union.size() >= 0) {
      @NonNull final List<@NonNull TimeState> result = union.headSet(new Entry<>(date), false)
                                                            .stream()
                                                            .map(Entry::getState)
                                                            .map(Duplicator::duplicate)
                                                            .sorted(this::sortByDateDescending)
                                                            .collect(Collectors.toList());

      if (cursor.hasLimit()) {
        return result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
      } else {
        return result.subList(cursor.getOffset(), result.size());
      }
    } else {
      return Collections.emptyList();
    }
  }

  private int sortByDateAscending (@NonNull final TimeState left, @NonNull final TimeState right) {
    return left.getEmissionDate().compareTo(right.getEmissionDate());
  }

  private int sortByDateDescending (@NonNull final TimeState left, @NonNull final TimeState right) {
    return -sortByDateAscending(left, right);
  }

  @Override
  public @NonNull List<@NonNull TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TreeSet<@NonNull Entry<TimeState>> union = getStatesOf(sensorIdentifiers);


    if (union.size() > 0) {
      @NonNull final List<@NonNull TimeState> result = union.tailSet(new Entry<>(date), false)
                                                            .stream()
                                                            .map(Entry::getState)
                                                            .map(Duplicator::duplicate)
                                                            .sorted(this::sortByDateAscending)
                                                            .collect(Collectors.toList());

      if (cursor.hasLimit()) {
        return result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
      } else {
        return result.subList(cursor.getOffset(), result.size());
      }
    } else {
      return Collections.emptyList();
    }

  }

  @Override
  public @NonNull List<@NonNull TimeState> find (
    @NonNull final Long sensorIdentifier, @NonNull final Cursor cursor
  ) {
    if (_statesBySensors.containsKey(sensorIdentifier)) {
      @NonNull final List<@NonNull TimeState> result = _statesBySensors.get(sensorIdentifier)
                                                                       .stream()
                                                                       .map(Entry::getState)
                                                                       .map(Duplicator::duplicate)
                                                                       .sorted(this::sortByDateAscending)
                                                                       .collect(Collectors.toList());

      if (cursor.hasLimit()) {
        return result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
      } else {
        return result.subList(cursor.getOffset(), result.size());
      }
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public @NonNull List<@NonNull TimeState> find (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TreeSet<@NonNull Entry<TimeState>> union = getStatesOf(sensorIdentifiers);

    if (union.size() > 0) {
      @NonNull final List<@NonNull TimeState> result = union.stream()
                                                            .map(Entry::getState)
                                                            .map(Duplicator::duplicate)
                                                            .sorted(this::sortByDateAscending)
                                                            .collect(Collectors.toList());

      if (cursor.hasLimit()) {
        return result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
      } else {
        return result.subList(cursor.getOffset(), result.size());
      }
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers
  )
  {
    @NonNull final TreeSet<@NonNull Entry<TimeState>> union = getStatesOf(sensorIdentifiers);

    if (union.size() > 0) {
      return Optional.of(union.last().getState());
    } else {
      return Optional.empty();
    }
  }

  private @NonNull TreeSet<@NonNull Entry<TimeState>> getStatesOf (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers
  )
  {
    if (sensorIdentifiers.size() == 0) return new TreeSet<>();

    @NonNull final TreeSet<@NonNull Entry<TimeState>> union;

    if (sensorIdentifiers.size() == 1) {
      union = _statesBySensors.getOrDefault(
        sensorIdentifiers.iterator().next(),
        new TreeSet<>()
      );
    } else {
      union = new TreeSet<>();

      for (@NonNull final Long sensor : sensorIdentifiers) {
        if (_statesBySensors.containsKey(sensor)) {
          union.addAll(_statesBySensors.get(sensor));
        }
      }
    }

    return union;
  }

  @Override
  public void onUpdate (
    @Nullable final ApplicationEntity oldEntity, @NonNull final ApplicationEntity newEntity
  )
  {
    super.onUpdate(oldEntity, newEntity);

    if (getManagedEntity().isInstance(newEntity)) {
      @Nullable final TimeState oldState = oldEntity == null ? null : getManagedEntity().cast(oldEntity);
      @NonNull final TimeState  newState = getManagedEntity().cast(newEntity);

      if (oldEntity != null) {
        _statesBySensors.get(oldState.getSensorIdentifier()).remove(new Entry<>(oldState));
      } else if (!_statesBySensors.containsKey(newState.getSensorIdentifier())) {
        _statesBySensors.put(
          newState.getSensorIdentifier(),
          new TreeSet<>()
        );
      }

      _statesBySensors.get(newState.getSensorIdentifier()).add(new Entry<>(newState));
    }
  }

  @Override
  public void onRemove (@NonNull final ApplicationEntity entity) {
    super.onRemove(entity);

    if (getManagedEntity().isInstance(entity)) {
      @NonNull final TimeState state = getManagedEntity().cast(entity);

      _statesBySensors.get(state.getSensorIdentifier()).remove(new Entry<>(state));

      if (_statesBySensors.get(state.getSensorIdentifier()).size() <= 0) {
        _statesBySensors.remove(state.getSensorIdentifier());
      }
    }
  }

  private static class Entry<TimeState extends State>
    implements Comparable<Entry<TimeState>>
  {
    @NonNull
    private final ZonedDateTime _emission;

    @Nullable
    private final TimeState _state;

    public Entry (@NonNull final TimeState state) {
      _state = state;
      _emission = state.getEmissionDate();
    }

    public Entry (@NonNull final ZonedDateTime emission) {
      _state = null;
      _emission = emission;
    }

    @Override
    public int compareTo (@NonNull final Entry<TimeState> other) {
      return this.getEmission().compareTo(other.getEmission());
    }

    public @NonNull ZonedDateTime getEmission () {
      return _emission;
    }

    public @Nullable TimeState getState () {
      return _state;
    }

    @Override
    public boolean equals (@Nullable final Object object) {
      if (object == this) return true;
      if (object == null) return false;

      if (object instanceof Entry) {
        final Entry<?> other = (Entry) object;

        return Objects.equals(_emission, other.getEmission());
      } else {
        return false;
      }
    }

    @Override
    public int hashCode () {
      return Objects.hash(_emission);
    }
  }
}
