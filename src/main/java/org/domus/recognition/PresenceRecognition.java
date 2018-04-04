package org.domus.recognition;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.HashMap;

import javax.persistence.EntityManager;

import org.domus.api.collection.EntityCollectionQuery;
import org.domus.api.collection.EntityCollections;
import org.domus.api.data.entity.BooleanState;
import org.domus.api.data.entity.Presence;
import org.domus.api.data.entity.Sensor;
import org.domus.api.data.repository.NodeRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PresenceRecognition implements Iterator<Presence>
{
  @NonNull
  private final EntityManager                  _entityManager;
  @NonNull
  private final NodeRepository                 _repository;
  @NonNull
  private final EntityCollections              _collections;

  @NonNull
  private final Sensor                         _sensor;
  @NonNull
  private final Iterator<Tick>         _ticks;

  @Nullable
  private Presence                             _lastPresence = null;
  @NonNull
  private final List<Presence>                 _next = new ArrayList<>();

  @NonNull
  private final EventTimeLines<Sensor>           _events = new EventTimeLines<Sensor>();
  
  @NonNull
  private final Map<Sensor, Tick>        _touchs = new HashMap<>();
  
  @NonNull
  private List<Map.Entry<Sensor, Event>> _solving = new ArrayList<>();

  public PresenceRecognition(@NonNull final ApplicationContext context, @NonNull final Sensor sensor) {
    if (sensor.getType().equals("common/virtual/presence")) {
      _entityManager = context.getBean(EntityManager.class);
      _repository = context.getBean(NodeRepository.class);
      _collections = context.getBean(EntityCollections.class);
      _sensor = sensor;
      _ticks = getTicks();
    } else {
      throw new Error(
        String.join("", "Invalid sensor type : ", sensor.getType(), ", common/virtual/presence expected.")
      );
    }
  }

  /**
   * Try to compute the next presence to return.
   */
  private void computeNext () {
    while (_ticks.hasNext() && _next.size() <= 0) {
      onTick(_ticks.next());
    }

    /*
     * ADD SOLVING if (_next.size() <= 0 && _lastPresence != null) {
     * _next.add(_lastPresence); _lastPresence = null; }
     */
  }

  private Iterator<Tick> getTicks () {
    final List<Sensor> sensors = _repository.getAllSensors(_sensor.getNodes(), "common/native/motion");

    final EntityCollectionQuery<BooleanState, BooleanState> stateQuery = _collections.createQuery(BooleanState.class);
    stateQuery.where(stateQuery.join("sensor").in(sensors));
    stateQuery.orderBy(_entityManager.getCriteriaBuilder().asc(stateQuery.field("date")));

    return new Ticks(_collections.fetch(stateQuery).iterator());
  }

  @Override
  public boolean hasNext () {
    if (_next.size() <= 0) computeNext();

    return _next.size() > 0;
  }

  @Override
  public Presence next () {
    if (_next.size() <= 0) computeNext();

    if (_next.size() <= 0) {
      throw new NoSuchElementException();
    }

    return _next.remove(0);
  }

  private void onBeginingTick (@NonNull final Tick tick) {
    //System.out.println("  BEGIN EVENT");
    _events.begin(tick.getSensor(), tick.getDate());
  }
  
  private void onEventDetection (@NonNull final Tick tick, @NonNull final Event event) {    
    _solving.add(new AbstractMap.SimpleEntry<>(tick.getSensor(), event));
    if (_solving.size() == 3) this.resolve();
  }

  private void onFinishingTick (@NonNull final Tick tick) {
    //System.out.println(" FINISH EVENT (" + _events.occuring(tick.getSensor()).getStart() + ")");

    if (_events.getEarlierOccuringEventKey().equals(tick.getSensor())) {
      //System.out.println("  REGISTERED");
      onEventDetection(tick, _events.terminate(tick.getSensor(), tick.getDate()));
    } else {
      //System.out.print("  DISCARDED");
      _events.forget(tick.getSensor());
    }    
  }

  private void onPresenceDetection (@NonNull final Sensor sensor, @NonNull final Event event) {
    onPresenceDetection(new Presence(event.getStart(), event.getEnd(), sensor.getNodes().get(0)));
  }

  private void onPresenceDetection (@NonNull final Entry<Sensor, Event> last) {
    onPresenceDetection(last.getKey(), last.getValue());
  }

  private void onPresenceDetection (@NonNull final Presence presence) {
    //System.out.println("  DETECT " + presence.getRoomName() + " from " + presence.getStart() + " to " + presence.getEnd());
    if (presence.getDuration().compareTo(Duration.ofSeconds(1)) <= 0) {
      //System.out.println("  DISCARDED");
    } else if (_lastPresence == null) {
      //System.out.println("  SAVE AND WAIT");
      _lastPresence = presence;
    } else if (_lastPresence.getRoom().equals(presence.getRoom())) {
      //System.out.println(" MERGE");
      _lastPresence = _lastPresence.merge(presence);
    } else {
      _lastPresence = _lastPresence.setEnd(presence.getStart());
      _next.add(_lastPresence);
      //System.out.println();
      //System.out.println("  | EMITING " + _lastPresence.getRoomName() + " from " + _lastPresence.getStart() + " to " + _lastPresence.getEnd());
      //System.out.println("  SAVE AND WAIT");
      _lastPresence = presence;
    }
  }

  private void onTick (@NonNull final Tick tick) {
    //System.out.println(tick);
    
    if (_events.hasOccuringEvent(tick.getSensor()) && tick.isDown()) {
      this.onFinishingTick(tick);
    } else if (!_events.hasOccuringEvent(tick.getSensor()) && tick.isUp()) {
      this.onBeginingTick(tick);
    }
  }

  private void resolve () {
    //System.out.println();
    //System.out.println("  RESOLVING...");
    /*for (final Map.Entry<Sensor, Event> entry : _solving) {
      System.out.println("    EVENT [" + entry.getKey().getNodes().get(0).getName() + "] from " + entry.getValue().getStart() + " to " + entry.getValue().getEnd());
    }*/

    final Map.Entry<Sensor, Event> first = _solving.get(0);
    final Map.Entry<Sensor, Event> second = _solving.get(1);
    final Map.Entry<Sensor, Event> last = _solving.get(2);

    if(first.getValue().hasEndOverlap(last.getValue())) {
      resolveAxC();
    } else if (first.getValue().hasEndOverlap(second.getValue()) && second.getValue().hasEndOverlap(last.getValue())) {
      resolveAxBxC();
    } else if (first.getValue().hasEndOverlap(second.getValue())) {
      resolveAxBC();
    } else if (second.getValue().hasEndOverlap(last.getValue())) {
      resolveABxC(); 
    } else {
      resolveABC(); 
    }
  }
  
  private void resolveABC () {
    //System.out.println("    ABC");
    final Map.Entry<Sensor, Event> first = _solving.remove(0);
    final Map.Entry<Sensor, Event> second =_solving.remove(0);
    final Map.Entry<Sensor, Event> last = _solving.get(0);
    
    final Duration secondDuration = Duration.between(second.getValue().getStart(), second.getValue().getEnd());
    final Duration innerDuration = Duration.between(first.getValue().getEnd(), last.getValue().getStart());
    final float secondFill = (secondDuration.getSeconds() + 1) / (float) (innerDuration.getSeconds() + 1);
    //System.out.println("      FILLING " + (secondFill * 100) + "%");
    
    // AxBxC
    if (secondFill >= 0.75) {
      _solving.add(0, second);
      _solving.add(0, first);
      this.resolveAxBxC();
    } else {
      // ABA
      if (first.getKey().equals(last.getKey())) {
        first.setValue(first.getValue().setEnd(second.getValue().getStart()));
        last.setValue(last.getValue().setStart(second.getValue().getEnd()));
      } 
      
      this.onPresenceDetection(first);
      this.onPresenceDetection(second);
    }
  }

  private void resolveABxC () {
    //System.out.println("    ABxC");
    final Map.Entry<Sensor, Event> first = _solving.remove(0);
    final Map.Entry<Sensor, Event> second =_solving.remove(0);
    final Map.Entry<Sensor, Event> last = _solving.get(0);
    
    final Duration secondDuration = Duration.between(second.getValue().getStart(), last.getValue().getStart());
    final Duration innerDuration = Duration.between(first.getValue().getEnd(), last.getValue().getStart());
    final float secondFill = (secondDuration.getSeconds() + 1) / (float) (innerDuration.getSeconds() + 1);
    //System.out.println("      FILLING " + (secondFill * 100) + "%");
    
    // AxBxC
    if (secondFill >= 0.75) {
      _solving.add(0, second);
      _solving.add(0, first);
      this.resolveAxBxC();
    } 
    // ABxA
    else if (secondFill <= 0.1 && first.getKey().equals(last.getKey())) {
      this.onPresenceDetection(first);
    } 
    // ABxC
    else {
      final Event finalSecond = second.getValue().resolveEndOverlap(last.getValue());
      final Event finalLast = last.getValue().resolveStartOverlap(second.getValue());
      last.setValue(finalLast);
      second.setValue(finalSecond);
      
      this.onPresenceDetection(first);
      this.onPresenceDetection(second);
    }
  }

  private void resolveAxBC () {
    //System.out.println("    AxBC");
    final Map.Entry<Sensor, Event> first = _solving.remove(0);
    final Map.Entry<Sensor, Event> second =_solving.remove(0);
    final Map.Entry<Sensor, Event> last = _solving.get(0);
    
    final Duration secondDuration = Duration.between(first.getValue().getEnd(), second.getValue().getEnd());
    final Duration innerDuration = Duration.between(first.getValue().getEnd(), last.getValue().getStart());
    final float secondFill = (secondDuration.getSeconds() + 1) / (float) (innerDuration.getSeconds() + 1);
    
    //System.out.println("      FILLING " + (secondFill * 100) + "%");
    // AxBxC
    if (secondFill >= 0.75) {
      _solving.add(0, second);
      _solving.add(0, first);
      this.resolveAxBxC();
    } 
    // AxBA
    else if (secondFill <= 0.1 && first.getKey().equals(last.getKey())) {
      this.onPresenceDetection(first);
    } 
    // AxBC
    else {
      final Event finalFirst = first.getValue().resolveEndOverlap(second.getValue());
      final Event finalSecond = second.getValue().resolveStartOverlap(first.getValue());
      first.setValue(finalFirst);
      second.setValue(finalSecond);
      
      this.onPresenceDetection(first);
      this.onPresenceDetection(second);
    }
  }

  private void resolveAxC () {
    //System.out.println("    AxC");
    final Map.Entry<Sensor, Event> first = _solving.remove(0);
    _solving.remove(0); // second
    final Map.Entry<Sensor, Event> last = _solving.get(0);
    
    final Event finalFirst = first.getValue().resolveEndOverlap(last.getValue());
    final Event finalLast = last.getValue().resolveStartOverlap(first.getValue());
    first.setValue(finalFirst);
    last.setValue(finalLast);
    
    this.onPresenceDetection(first);
  }

  private void resolveAxBxC () {
    //System.out.println("    AxBxC");
    final Map.Entry<Sensor, Event> first = _solving.remove(0);
    final Map.Entry<Sensor, Event> second = _solving.remove(0);
    final Map.Entry<Sensor, Event> last = _solving.get(0);
    
    final Event finalFirst = first.getValue().resolveEnd(second.getValue());
    final Event finalSecond =  second.getValue().resolveStart(first.getValue()).resolveEnd(last.getValue());
    final Event finalLast = last.getValue().resolveStart(second.getValue());
    first.setValue(finalFirst);
    second.setValue(finalSecond);
    last.setValue(finalLast);
   
    this.onPresenceDetection(first);
    this.onPresenceDetection(second);
  }
  
  public Stream<Presence> toStream () {
    final Spliterator<Presence> spliterator = Spliterators.spliteratorUnknownSize(
      this, 
      Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.DISTINCT
    );
    
    return StreamSupport.stream(spliterator, false);
  }

  public Stream<Presence> toStream (final boolean parallel) {
    final Spliterator<Presence> spliterator = Spliterators.spliteratorUnknownSize(
      this, 
      Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.DISTINCT
    );
    
    return StreamSupport.stream(spliterator, parallel);
  }
}
