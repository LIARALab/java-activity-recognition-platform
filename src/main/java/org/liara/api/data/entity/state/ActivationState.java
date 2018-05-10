/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.data.entity.state;

import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "states_presence")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(ActivationStateCreationSchema.class)
@UseMutationSchema(ActivationStateMutationSchema.class)
public class ActivationState extends State
{  
  public static EntityFieldSelector<ActivationState, Expression<ZonedDateTime>> START_DATE = (query, queried) -> {
    return queried.join("_startState").get("_emittionDate");
  };
  
  public static EntityFieldSelector<ActivationState, Expression<ZonedDateTime>> END_DATE = (query, queried) -> {
    return queried.join("_endState").get("_emittionDate");
  };
  
  public static EntityFieldSelector<ActivationState, Expression<Long>> DURATION_SELECTOR = (query, queried) -> {
    final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
    final Expression<ZonedDateTime> start = START_DATE.select(query, queried);
    final Expression<ZonedDateTime> end = END_DATE.select(query, queried);
    
    return builder.sum(
      builder.prod(
        builder.function(
          "DATEDIFF", Long.class, start, end
        ), 24L * 3600L
      ),
      builder.toLong(
        builder.quot(
          builder.function(
            "TIMESTAMPDIFF_MICROSECOND", Long.class, start, end
          ), 1000L
        )
      )
    );
  };
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "start_state_identifier", nullable = false, unique = false, updatable = true)
  private State _startState;

  @ManyToOne(optional = true)
  @JoinColumn(name = "end_state_identifier", nullable = true, unique = false, updatable = true)
  private State _endState;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = true)
  private Node _node;
  
  public ActivationState () { }
  
  public ActivationState (@NonNull final ActivationStateCreationSchema schema) {
    super (schema);
    
    _startState = EntityCollections.STATES.findByIdentifier(schema.getStartState()).get();
    
    if (schema.getEndState() != null) {
      _endState = EntityCollections.STATES.findByIdentifier(schema.getEndState()).get();
    } else {
      _endState = null;
    }
    
    _node = EntityCollections.NODES.findByIdentifier(schema.getNode()).get();
  }
  
  public Duration getDuration () {
    if (_endState == null) {
      return null;
    } else {
      return Duration.between(_startState.getEmittionDate(), _endState.getEmittionDate());
    }
  }
  
  public Long getMilliseconds () {
    final Duration duration = getDuration();
    
    if (duration == null) {
      return null;
    } else {
      return getDuration().getNano() / 1_000_000L;
    }
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getEnd () {
    return (_endState == null) ? null : _endState.getEmittionDate();
  }
  
  @JsonIgnore
  public State getEndState () {
    return _endState;
  }
  
  public Long getEndStateIdentifier () {
    if (_endState == null) return null;
    else return _endState.getIdentifier();
  }
  
  public void setEndStateIdentifier (@Nullable final Long identifier) {
    if (identifier == null) {
      _endState = null;
    } else {
      _endState = EntityCollections.STATES.findByIdentifier(identifier).get();
    }
  }

  @JsonIgnore
  public Node getNode () {
    return _node;
  }
  
  public Long getNodeIdentifier () {
    return _node.getIdentifier();
  }

  public void setNodeIdentifier (@NonNull final Long node) {
    _node = EntityCollections.NODES.findByIdentifier(node).get();
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getStart () {
    return _startState.getEmittionDate();
  }
  
  @JsonIgnore
  public State getStartState () {
    return _startState;
  }
  
  public Long getStartStateIdentifier () {
    return _startState.getIdentifier();
  }

  public void setStartStateIdentifier (@NonNull final Long identifier) {
    _startState = EntityCollections.STATES.findByIdentifier(identifier).get();
  }
  
  public void setEndState (@Nullable final State state) {
    _endState = state;
 }

  public void setNode (@NonNull final Node node) {
    _node = node;
  }

  public void setStartState (@NonNull final State start) {
    _startState = start;
  }
  
  @Override
  public ActivationStateSnapshot snapshot () {
    return new ActivationStateSnapshot(this);
  }
}
