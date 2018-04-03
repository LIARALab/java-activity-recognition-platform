package org.domus.recognition;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.domus.api.data.entity.BooleanState;
import org.domus.api.data.entity.Sensor;
import org.springframework.lang.NonNull;

public class Ticks implements Iterator<Tick>
{
  @NonNull
  private final Iterator<BooleanState> _states;
  
  private LocalDateTime _now = null;
  
  @NonNull
  private final Map<Sensor, Tick> _latest = new HashMap<>();
  
  @NonNull
  private final SortedSet<Tick> _emitted = new TreeSet<>();

  @NonNull
  private final Duration _maxEventDuration;
  
  @NonNull
  private final Duration _safeDuration;

  public Ticks (@NonNull final Iterator<BooleanState> states) {
    _states = states;
    _maxEventDuration = Duration.ofSeconds(20);
    _safeDuration = _maxEventDuration.multipliedBy(2);
  }

  public Ticks (@NonNull final Iterable<BooleanState> states) {
    _states = states.iterator();
    _maxEventDuration = Duration.ofSeconds(20);
    _safeDuration = _maxEventDuration.multipliedBy(2);
  }

  public Ticks (@NonNull final BooleanState... states) {
    _states = Arrays.asList(states).iterator();
    _maxEventDuration = Duration.ofSeconds(20);
    _safeDuration = _maxEventDuration.multipliedBy(2);
  }
  
  @Override
  public boolean hasNext () {
    update();
    return _emitted.size() > 0;
  }

  @Override
  public Tick next () {
    update();
    return emitNextTick();
  }

  private Tick emitNextTick () {
    if (_emitted.size() <= 0) throw new NoSuchElementException();
    
    //System.out.println("CHOOSE TICK TO EMIT BETWEEN :");
    //for (final Tick tick : _emitted) {
      //System.out.println("  | " + tick);
    //}
    
    
    final Tick next = _emitted.first();
    _emitted.remove(next);
    //System.out.println("EMIT " + next);
    return next;
  }
  
  private void update () {
    while (haveToUpdate()) {
      //System.out.println("UPDATE TICKS");
      //System.out.println("  CURRENT TIME : " + ((_now == null) ? null : _now));
      final Tick next = getNextTick();
      //System.out.println("  NEXT TICK : " + next);
      progress(next.getDate());

      final Tick previous = getPreviousTick(next);
      //System.out.println("  NEXT TICK : " + next);
      //System.out.println("  PREVIOUS TICK : " + previous);
      
      if (!next.equals(previous)) {
        if (previous == null || previous.isUp() != next.isUp()) {
          emit(next); 
        }
        
        if (previous != null && previous.isDown() && next.isDown() && Duration.between(previous.getDate(), next.getDate()).compareTo(_maxEventDuration) > 0) {
          emit(Tick.before(next, _maxEventDuration).setUp());
          emit(next);
        }
        
        if (previous == null && next.isDown()) {
          //System.out.println("  DEAD TICK FOUND : " + Tick.before(next, _maxEventDuration).setDown());
          emit(Tick.before(next, _maxEventDuration).setUp());
        }
        
        _latest.put(next.getSensor(), next);
      }
    }
  }

  private boolean haveToUpdate () {
    return _states.hasNext() && (
      _emitted.size() <= 0 || 
      Duration.between(_emitted.first().getDate(), _now).compareTo(_safeDuration) <= 0
    );
  }

  private Tick getNextTick () {
    return new Tick(_states.next());
  }

  private Tick getPreviousTick (@NonNull final Tick next) {
    if (_latest.containsKey(next.getSensor())) {
      return _latest.get(next.getSensor());
    }
    
    return null;
  }
  
  private void progress (@NonNull final LocalDateTime time) {
    if (_now != null && _now.isAfter(time)) {
      throw new Error("Unnable to progress to a previous date.");
    }
    //System.out.println("  PROGRESS TO " + time);
    
    final List<Tick> deadTicks = new ArrayList<>();
    
    for (final Map.Entry<Sensor, Tick> entry : _latest.entrySet()) {
      //System.out.println("  | CHECK " + entry.getValue() + " OF " + entry.getKey().getNodes().get(0).getName());
      if (entry.getValue().isUp() && Duration.between(entry.getValue().getDate(), time).compareTo(_maxEventDuration) > 0) {
        //System.out.println("  |   DEAD TICK FOUND " + Tick.after(entry.getValue(), _maxEventDuration).setDown());
        deadTicks.add(Tick.after(entry.getValue(), _maxEventDuration).setDown());
      }
    }
 
    for (final Tick deadTick : deadTicks) {
      emit(deadTick);
      _latest.put(deadTick.getSensor(), deadTick);
    }
    
    _now = time;
    //System.out.println("  PROGRESS DONE TO " + _now);
  }

  private void emit (@NonNull final Tick tick) {
    if (_emitted.contains(tick)) {
      throw new Error("Trying to emit " + tick + " two times !");
    }
    _emitted.add(tick);
  }
}
