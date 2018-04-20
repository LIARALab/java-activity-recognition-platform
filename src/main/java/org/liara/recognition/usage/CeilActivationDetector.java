package org.liara.recognition.usage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.data.entity.state.PresenceState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CeilActivationDetector
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull
  private final Sensor _sensor;
  
  @NonNull
  private final Sensor _target; 
  
  @NonNull
  private final double _ceil;
  
  @Nullable
  private DoubleState _last = null;
  
  @Nullable
  private PresenceState _lastEmittion = null;
  
  @NonNull
  private final List<PresenceState> _invalidStates = new ArrayList<>();
  
  public CeilActivationDetector (
    @NonNull final EntityManager manager,
    @NonNull final Sensor sensor,
    @NonNull final Sensor target,
    @NonNull final double ceil
  ) {
    _manager = manager;
    _sensor = sensor;
    _target = target;
    _ceil = ceil;
  }

  public void onStateAddition (@NonNull final State state) {
    if (state instanceof DoubleState) {
      onDoubleStateAddition(DoubleState.class.cast(state));
    }
  }
  
  private void onDoubleStateAddition (@NonNull final DoubleState state) {
    if (state.getSensor().equals(_target)) {
      onTargetStateAddition(state);
    }
  }

  private void onTargetStateAddition (@NonNull final DoubleState state) {
    if (_last == null) {
      _last = state;
    } else {
      final ZonedDateTime current = _last.getEmittionDate();
      final ZonedDateTime next = state.getEmittionDate();
      
      if (current.isBefore(next) || current.equals(next)) {
        onNextStateAddition(state);
      } else {
        final DoubleState endState = _last;
        
        backtrack(state);
        replay(state, endState);
      }
    }
  }
  
  private void onNextStateAddition (@NonNull final DoubleState state) {
    final double lastValue = _last.getValue();
    final double nextValue = state.getValue();
    
    if (lastValue <= _ceil && nextValue > _ceil) {
      onActivation(state.getEmittionDate());
    } else if (lastValue >= _ceil && nextValue < _ceil) {
      onDeactivation(_last.getEmittionDate());
    }
    
    _last = state;
  }

  private void onDeactivation (@NonNull final ZonedDateTime emittionDate) {
    if (_lastEmittion != null && _lastEmittion.getEnd() == null) {
      _lastEmittion.setEnd(emittionDate);
      _manager.merge(_lastEmittion);
      _lastEmittion = null;
    }
  }

  private void onActivation (@NonNull final ZonedDateTime emittionDate) {
    if (_lastEmittion == null || _lastEmittion.getEnd() != null) {
      _lastEmittion = next();
      _lastEmittion.setEmittionDate(emittionDate);
      _lastEmittion.setStart(emittionDate);
      _lastEmittion.setEnd(null);
      _lastEmittion.setSensor(_sensor);
      _lastEmittion.setNode(_sensor.getNodes().get(0));
      if (_lastEmittion.getIdentifier() != null) _manager.merge(_lastEmittion);
      else _manager.persist(_lastEmittion);
    }
  }
  
  private PresenceState next () {
    if (_invalidStates.size() > 0) {
      final PresenceState next = _invalidStates.remove(0);
      next.restore();
      return next;
    } else {
      return new PresenceState();
    }
  }

  private void replay (
    @NonNull final DoubleState start,
    @NonNull final DoubleState end
  ) {
    System.out.println("REPLAYING...");
    final List<DoubleState> states = _manager.createQuery(
      String.join(
        " ",
        "SELECT state FROM DoubleState state",
        "WHERE state._sensor = :observed",
        "  AND state._emittionDate BETWEEN :start AND :end",
        "  AND state._deletionDate IS NULL",
        "ORDER BY state._emittionDate"
      ), DoubleState.class
    ).setParameter("observed", _target)
     .setParameter("start", start.getEmittionDate())
     .setParameter("end", end.getEmittionDate())
     .getResultList();
    
    for (final DoubleState state : states) {
      onTargetStateAddition(state);
    }
    System.out.println("DONE");
  }

  private void backtrack (
    @NonNull final DoubleState target
  ) {
    // System.out.println("/-------------------------------------");
 // System.out.println("BACKTRACKING FROM " + _last);
 // System.out.println("               TO " + target);
 // System.out.println("     LAST EMITTED " + _lastEmittion);
 // System.out.println("   INVALID STATES " + _invalidStates.size());
 // System.out.println(" - Deleting invalid emitted states...");
    deleteInvalidStates(target);
 // System.out.println(" - Refreshing invalid states stack...");
    refreshInvalidStates(target);
 // System.out.println(" - Refreshing last emitted...");
    refreshLastEmitted(target);
 // System.out.println(" - Refreshing last tick...");
    refreshLastTick(target);
 // System.out.println("   BACKTRACKED TO " + target);
 // System.out.println("             LAST " + _last);
 // System.out.println("     LAST EMITTED " + _lastEmittion);
 // System.out.println("   INVALID STATES " + _invalidStates.size());
 // System.out.println("/-------------------------------------");
  }

  private void refreshLastTick (@NonNull final DoubleState target) {
    final List<DoubleState> last = _manager.createQuery(
      String.join(
        " ",
        "SELECT state",
        "FROM DoubleState state",
        "WHERE state._sensor = :observed",
        "  AND state._deletionDate IS NULL",
        "  AND state._emittionDate < :limitDate",
        "ORDER BY state._emittionDate DESC"
      ), DoubleState.class
    ).setParameter("observed", _target)
     .setParameter("limitDate", target.getEmittionDate())
     .setMaxResults(1)
     .getResultList();
    
    if (last.size() > 0) {
      _last = last.get(0);
    } else {
      _last = null;
    }
  }

  private void refreshLastEmitted (@NonNull final DoubleState target) {
    final List<PresenceState> lastEmitted = _manager.createQuery(
      String.join(
        " ", 
        "SELECT presenceState",
        "FROM PresenceState presenceState",
        "WHERE presenceState._sensor = :target",
        "  AND presenceState._deletionDate IS NULL",
        "ORDER BY presenceState._emittionDate DESC"
      ), PresenceState.class
    ).setParameter("target", _sensor)
     .setMaxResults(1)
     .getResultList();
    
    if (lastEmitted.size() > 0) {
      _lastEmittion = lastEmitted.get(0);
      final ZonedDateTime invalidDate = target.getEmittionDate();
      
      if (_lastEmittion.getEnd() != null && _lastEmittion.getEnd().isBefore(invalidDate)) {
        _lastEmittion = null;
      } else if (
          _lastEmittion.getEnd() != null && (
            _lastEmittion.getEnd().isAfter(invalidDate) ||
            _lastEmittion.getEnd().isEqual(invalidDate)
          )
      ) {
        _lastEmittion.setEnd(null);
        _lastEmittion.setEmittionDate(_lastEmittion.getStart());
        
        if (
            _lastEmittion.getStart().isAfter(invalidDate) ||
            _lastEmittion.getStart().isEqual(invalidDate)
          ) {
            _lastEmittion.delete();
            _invalidStates.add(0, _lastEmittion);
            _manager.merge(_lastEmittion);
            _lastEmittion = null;
        } else {
          _manager.merge(_lastEmittion);
        }
      }
    } else {
      _lastEmittion = null;
    }
  }

  private void refreshInvalidStates (@NonNull final DoubleState target) {
    _invalidStates.clear();
    _invalidStates.addAll(
      _manager.createQuery(
        String.join(
          " ", 
          "SELECT presenceState",
          "FROM PresenceState presenceState",
          "WHERE presenceState._sensor = :target",
          "  AND presenceState._deletionDate IS NOT NULL"
        ), PresenceState.class
      ).setParameter("target", _sensor)
       .getResultList()
    );
  }

  private void deleteInvalidStates (@NonNull final DoubleState target) {
    _manager.flush();
    _manager.clear();
    _manager.createNativeQuery(String.join(
      " ",
      "UPDATE states",
      "SET deleted_at = :deletionDate",
      "WHERE sensor_identifier = :target",
      "  AND emitted_at > :invalidationDate"
    )).setParameter("deletionDate", ZonedDateTime.now())
      .setParameter("target", _sensor.getIdentifier())
      .setParameter("invalidationDate", target.getEmittionDate())
      .executeUpdate();
  }

  public PresenceState getLastEmittion () {
    return _lastEmittion;
  }
}
