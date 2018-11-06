package org.liara.api.data.repository.local;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.StateRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LocalStateRepository<TimeState extends State>
       extends LocalApplicationEntityRepository<TimeState>
  implements StateRepository<TimeState>
{
  @Override
  public List<TimeState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  )
  {
    if (!_statesBySensors.containsKey(sensor.getIdentifier())) {
      return Collections.emptyList();
    }

    final List<TimeState> result = new ArrayList<>();

    _statesBySensors.get(sensor.getIdentifier())
                    .headSet(new Entry<>(date), false)
                    .stream()
                    .forEach(x -> result.add(x.getState()));

    Collections.sort(result, this::sortByDateDescending);

    return result;
  }
  
  @NonNull
  private final Map<Long, TreeSet<Entry<TimeState>>> _statesBySensors = new HashMap<>();
  
  @NonNull
  private final Map<String, Map<Long, Set<Long>>> _correlations = new HashMap<>();

  public LocalStateRepository (@NonNull final Class<TimeState> type) {
    super(type);
  }

  public static <TimeState extends State> LocalStateRepository<TimeState> create (
    @NonNull final LocalEntityManager entityManager,
    @NonNull final Class<TimeState> type
  ) {
    final LocalStateRepository<TimeState> result = new LocalStateRepository<>(type);
    result.setParent(entityManager);
    return result;
  }
  
  private int sortByDateAscending (@NonNull final TimeState left, @NonNull final TimeState right) {
    return left.getEmissionDate().compareTo(right.getEmissionDate());
  }
  
  private int sortByDateDescending (@NonNull final TimeState left, @NonNull final TimeState right) {
    return -sortByDateAscending(left, right);
  }

  @Override
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    final int count
  ) {
    final List<TimeState> result = findAllPrevious(date, sensor);

    if (count < result.size()) {
      return result.subList(0, count);
    } else {
      return result;
    }
  }
  
  @Override
  public List<TimeState> findAllNext (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    if (!_statesBySensors.containsKey(sensor.getIdentifier())) {
      return Collections.emptyList();
    }

    final List<TimeState> result = new ArrayList<>();

    _statesBySensors.get(sensor.getIdentifier())
                    .tailSet(new Entry<>(date), false)
                    .stream().forEach(x -> result.add(x.getState()));

    return result;
  }

  @Override
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    final int count
  ) {
    final List<TimeState> result = findAllNext(date, sensor);

    if (count < result.size()) {
      return result.subList(0, count);
    } else {
      return result;
    }
  }

  @Override
  public List<TimeState> find (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor, final int offset,
    final int count
  ) {
    final List<TimeState> result = findAll(sensor);

    if (offset + count >= result.size()) {
      return result.subList(offset, result.size());
    }

    return result.subList(offset, offset + count);
  }

  @Override
  public List<TimeState> findAll (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    if (!_statesBySensors.containsKey(sensor.getIdentifier())) {
      return Collections.emptyList();
    }

    final List<TimeState> result = new ArrayList<>();
    _statesBySensors.get(
      sensor.getIdentifier()
    ).stream().forEach(x -> result.add(x.getState()));

    return result;
  }

  @Override
  public List<TimeState> findWithCorrelation (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    if (!_correlations.containsKey(name)) {
      return Collections.emptyList();
    }

    if (!_correlations.get(name).containsKey(correlated.getIdentifier())) {
      return Collections.emptyList();
    }

    final List<TimeState> results = new ArrayList<>();

    for (final Long potentialResult : _correlations.get(name).get(correlated.getIdentifier())) {
      final TimeState state = find(potentialResult).get();

      if (state.getSensor().equals(sensor)) {
        results.add(state);
      }
    }

    return results;
  }

  @Override
  public List<TimeState> findWithCorrelations (
    @NonNull final Map<String, ApplicationEntityReference<? extends State>> correlations,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    Set<Long> potentialResults = null;

    for (final Map.Entry<String, ApplicationEntityReference<? extends State>> correlation : correlations.entrySet()) {
      if (!_correlations.containsKey(correlation.getKey())) {
        return Collections.emptyList();
      }

      if (!_correlations.get(correlation.getKey()).containsKey(correlation.getValue().getIdentifier())) {
        return Collections.emptyList();
      }

      if (potentialResults == null) {
        potentialResults = new HashSet<>(
          _correlations.get(correlation.getKey())
                       .get(correlation.getValue().getIdentifier())
        );
      } else {
        potentialResults.retainAll(
          _correlations.get(correlation.getKey())
                       .get(correlation.getValue().getIdentifier())
        );
      }
    }

    if (potentialResults.size() <= 0) {
      return Collections.emptyList();
    }

    final List<TimeState> results = new ArrayList<>();

    for (final Long potentialResult : potentialResults) {
      final TimeState state = find(potentialResult).get();

      if (state.getSensor().equals(sensor)) {
        results.add(state);
      }
    }

    return results;
  }

  public Set<TimeState> findWithCorrelation (
    @NonNull final String name
  )
  {
    if (!_correlations.containsKey(name)) {
      return Collections.emptySet();
    }

    final Set<Long> results = new HashSet<>();

    _correlations.get(name).values().forEach(results::addAll);

    return results.stream().map(this::find).map(Optional::get).collect(Collectors.toSet());
  }

  @Override
  public List<TimeState> findWithAnyCorrelation (
    @NonNull final Collection<String> keys,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  )
  {
    final Set<Long> potentialResults = new HashSet<>();

    for (final String key : keys) {
      if (!_correlations.containsKey(key)) {
        return Collections.emptyList();
      }

      if (_correlations.get(key).containsKey(correlated.getIdentifier())) {
        potentialResults.addAll(_correlations.get(key).get(correlated.getIdentifier()));
      }
    }

    if (potentialResults.size() <= 0) {
      return Collections.emptyList();
    }

    final List<TimeState> results = new ArrayList<>();

    for (final Long potentialResult : potentialResults) {
      final TimeState state = find(potentialResult).get();

      if (state.getSensor().equals(sensor)) {
        results.add(state);
      }
    }

    return results;
  }

  @Override
  protected void trackedEntityWasAdded (@NonNull final TimeState entity) {
    if (isRegistered(entity)) return;

    if (!_statesBySensors.containsKey(entity.getSensor().getIdentifier())) {
      _statesBySensors.put(entity.getSensor().getIdentifier(), new TreeSet<>());
    }

    _statesBySensors.get(entity.getSensor().getIdentifier()).add(new Entry<>(entity));

    for (final Map.Entry<String, State> correlation : entity.getCorrelations().entrySet()) {
      if (!_correlations.containsKey(correlation.getKey())) {
        _correlations.put(correlation.getKey(), new HashMap<>());
      }

      final Map<Long, Set<Long>> correlationsByKey = _correlations.get(correlation.getKey());

      if (!correlationsByKey.containsKey(correlation.getValue().getIdentifier())) {
        correlationsByKey.put(correlation.getValue().getIdentifier(), new HashSet<>());
      }

      correlationsByKey.get(correlation.getValue().getIdentifier()).add(
        entity.getIdentifier()
      );
    }
  }

  @Override
  protected void trackedEntityWasRemoved (@NonNull final TimeState entity) {
    if (!isRegistered(entity)) return;

    _statesBySensors.get(entity.getSensor().getIdentifier()).remove(new Entry<>(entity));

    if (_statesBySensors.get(entity.getSensor().getIdentifier()).size() <= 0) {
      _statesBySensors.remove(entity.getSensor().getIdentifier());
    }

    for (final Map.Entry<String, State> correlation : entity.getCorrelations().entrySet()) {
      decorrelate(entity, correlation.getKey(), correlation.getValue());
    }

    super.remove(entity);
  }

  private boolean isRegistered (@NonNull final TimeState entity) {
    return _statesBySensors.containsKey(entity.getSensor().getIdentifier()) &&
           _statesBySensors.get(entity.getSensor().getIdentifier()).contains(new Entry<State>(entity));
  }
  
  private void decorrelate (
    @NonNull final TimeState state, 
    @NonNull final String key, 
    @NonNull final State correlation
  ) {
    if (_correlations.containsKey(key) && _correlations.get(key).containsKey(correlation.getIdentifier())) {
      _correlations.get(key)
                   .get(correlation.getIdentifier())
                   .remove(state.getIdentifier());
      
      if (_correlations.get(key).get(correlation.getIdentifier()).size() <= 0) {
      _correlations.get(key).remove(correlation.getIdentifier());
      }
      
      if (_correlations.get(key).size() <= 0) {
      _correlations.remove(key);
      }
    }
  }
  
  @Override
  public List<TimeState> findAll (@NonNull final Collection<ApplicationEntityReference<? extends Sensor>> sensors) {
    final List<TimeState>                                   result        = new ArrayList<TimeState>();
    final Set<ApplicationEntityReference<? extends Sensor>> uniqueSensors = new HashSet<>(sensors);

    uniqueSensors.stream().map(this::findAll).forEach(states -> result.addAll(states));

    Collections.sort(result, this::sortByDateAscending);

    return result;
  }

  @Override
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final int count
  ) {
    final List<TimeState> result = new ArrayList<>(count * inputSensors.size());

    new HashSet<>(
        inputSensors
    ).stream().map(reference -> findPrevious(date, reference, count))
     .forEach(result::addAll);

    Collections.sort(result, this::sortByDateDescending);

    return result.subList(0, count);
  }

  @Override
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final int count
  ) {
    final List<TimeState> result = new ArrayList<>(count * inputSensors.size());

    new HashSet<>(
        inputSensors
    ).stream().map(reference -> findNext(date, reference, count))
     .forEach(result::addAll);

    Collections.sort(result, this::sortByDateAscending);

    return result.subList(0, count);
  }

  @Override
  public List<TimeState> findAllNext (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    final List<TimeState> result = new ArrayList<>();

    inputSensors.stream()
                .map(x -> this.findAllNext(date, x))
                .forEach(x -> result.addAll(x));

    Collections.sort(result, this::sortByDateAscending);

    return result;
  }

  @Override
  public List<TimeState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    final List<TimeState> result = new ArrayList<>();

    inputSensors.stream()
                .map(x -> this.findAllPrevious(date, x))
                .forEach(x -> result.addAll(x));

    Collections.sort(result, this::sortByDateDescending);

    return result;
  }

  private static class Entry<TimeState extends State>
    implements Comparable<Entry<TimeState>>
  {
    @NonNull
    private final ZonedDateTime _emittion;

    @Nullable
    private final TimeState _state;

    public Entry (@NonNull final TimeState state) {
      _state = state;
      _emittion = state.getEmissionDate();
    }

    public Entry (@NonNull final ZonedDateTime emittion) {
      _state = null;
      _emittion = emittion;
    }

    @Override
    public int compareTo (@NonNull final Entry<TimeState> other) {
      return this.getEmittion().compareTo(other.getEmittion());
    }

    public ZonedDateTime getEmittion () {
      return _emittion;
    }

    public TimeState getState () {
      return _state;
    }

    @Override
    public boolean equals (@NonNull final Object object) {
      if (object == this) return true;
      if (object == null) return false;

      if (object instanceof Entry) {
        final Entry<?> other = (Entry) object;

        return Objects.equals(_emittion, other.getEmittion());
      } else {
        return false;
      }
    }

    @Override
    public int hashCode () {
      return Objects.hash(_emittion);
    }
  }
}
