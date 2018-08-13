package org.liara.api.data.repository.local;

import org.liara.api.data.Conjunction;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ConjunctionRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.*;

public class LocalConjunctionRepository
  extends BaseLocalRepository
  implements ConjunctionRepository
{
  private static class Entry
    implements Comparable<Entry>
  {
    @NonNull
    private final ZonedDateTime _index;

    @Nullable
    private final ActivationState _state;

    public Entry (@NonNull final ZonedDateTime index) {
      _index = index;
      _state = null;
    }

    public Entry (
      @NonNull final ZonedDateTime index,
      @NonNull final ActivationState state
    )
    {
      _index = index;
      _state = state;
    }

    @Override
    public int compareTo (@NonNull final Entry other) {
      return _index.compareTo(other.getIndex());
    }

    public ZonedDateTime getIndex () {
      return _index;
    }

    public ActivationState getState () {
      return _state;
    }

    @Override
    public boolean equals (@Nullable final Object other) {
      if (other == this) return true;
      if (other == null) return false;

      if (other instanceof Entry) {
        final Entry otherEntry = Entry.class.cast(other);
        return Objects.equals(otherEntry.getIndex(), _index);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode () {
      return Objects.hashCode(_index);
    }
  }

  private static int ascConjunctionComparator (
    @NonNull final Conjunction left,
    @NonNull final Conjunction right
  )
  {
    return left.getStart()
               .compareTo(right.getStart());
  }

  @NonNull
  private final Map<ApplicationEntityReference<Sensor>, List<TreeSet<Entry>>> _statesBySensors = new HashMap<>();

  @Override
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> inputs
  )
  {
    final Iterator<ApplicationEntityReference<Sensor>> inputsIterator = inputs.iterator();
    final List<List<TreeSet<Entry>>>                   activations    = new ArrayList<>(inputs.size());

    while (inputsIterator.hasNext()) {
      final ApplicationEntityReference<Sensor> sensor = inputsIterator.next();
      if (_statesBySensors.containsKey(sensor)) {
        activations.add(_statesBySensors.get(sensor));
      } else {
        return Collections.emptyList();
      }
    }

    return computeConjunctions(activations);
  }

  private List<Conjunction> computeConjunctions (@NonNull final List<List<TreeSet<Entry>>> activations) {
    final TreeSet<Conjunction>   result     = new TreeSet<>(LocalConjunctionRepository::ascConjunctionComparator);
    final List<SortedSet<Entry>> collisions = new ArrayList<>(activations.size());

    for (final Entry state : activations.get(0)
                                        .get(0)) {
      for (int index = 1; index < activations.size(); ++index) {
        collisions.add(activations.get(index)
                                  .get(0)
                                  .headSet(new Entry(state.getState()
                                                          .getEnd()))
                                  .tailSet(new Entry(activations.get(index)
                                                                .get(1)
                                                                .tailSet(new Entry(state.getState()
                                                                                        .getStart()))
                                                                .first()
                                                                .getState()
                                                                .getStart())));
      }

      computeConjunctions(state, collisions, result);
      collisions.clear();
    }

    return new ArrayList<>(result);
  }


  private void computeConjunctions (
    @NonNull final Entry state,
    @NonNull final List<SortedSet<Entry>> collisions,
    @NonNull final TreeSet<Conjunction> result
  )
  {
  }

  private void computeConjunctionsStep (
    @NonNull final ZonedDateTime start,
    @NonNull final ZonedDateTime end,
    @NonNull final List<SortedSet<Entry>> collisions,
    @NonNull final List<ActivationState> path,
    @NonNull final TreeSet<Conjunction> result
  )
  {
    if (path.size() <= collisions.size()) {
      for (final Entry entry : collisions.get(path.size() - 1)) {
        final ActivationState state = entry.getState();

        if (state.getStart()
                 .compareTo(end) < 0 && state.getEnd()
                                             .compareTo(start) > 0) {
          path.add(state);
          computeConjunctionsStep(
            (start.compareTo(state.getStart()) > 0) ? start : state.getStart(),
            (end.compareTo(state.getEnd()) < 0) ? end : state.getEnd(),
            collisions,
            path,
            result
          );
          path.remove(path.size() - 1);
        }
      }
    } else {
      result.add(new Conjunction(path));
    }
  }

  private void initializeIfNotAlreadyAdded (@NonNull final ApplicationEntityReference<Sensor> sensor) {
    if (!_statesBySensors.containsKey(sensor)) {
      _statesBySensors.put(sensor, Arrays.asList(new TreeSet<Entry>(), new TreeSet<Entry>()));
    }
  }

  private void destroyIfEmpty (@NonNull final ApplicationEntityReference<Sensor> sensor) {
    if (_statesBySensors.get(sensor)
                        .size() <= 0) {
      _statesBySensors.remove(sensor);
    }
  }

  @Override
  protected void entityWasAdded (@NonNull final ApplicationEntity entity) {
    super.entityWasAdded(entity);

    if (entity instanceof ActivationState) {
      final ActivationState state = ActivationState.class.cast(entity);

      initializeIfNotAlreadyAdded(state.getSensor()
                                       .getReference());

      _statesBySensors.get(state.getSensor()
                                .getReference())
                      .get(0)
                      .add(new Entry(state.getStart(), state));
      _statesBySensors.get(state.getSensor()
                                .getReference())
                      .get(1)
                      .add(new Entry(state.getEnd(), state));
    }
  }

  @Override
  protected void entityWasRemoved (@NonNull final ApplicationEntity entity) {
    super.entityWasRemoved(entity);

    if (entity instanceof ActivationState) {
      final ActivationState state = ActivationState.class.cast(entity);

      _statesBySensors.get(state.getSensor()
                                .getReference())
                      .get(0)
                      .remove(new Entry(state.getStart(), state));
      _statesBySensors.get(state.getSensor()
                                .getReference())
                      .get(1)
                      .remove(new Entry(state.getEnd(), state));

      destroyIfEmpty(state.getSensor()
                          .getReference());
    }
  }
}
