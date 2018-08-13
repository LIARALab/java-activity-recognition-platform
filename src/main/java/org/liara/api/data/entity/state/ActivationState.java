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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.time.Duration;
import java.time.ZonedDateTime;

@Entity
@Table(name = "states_presence")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(ActivationStateCreationSchema.class)
@UseMutationSchema(ActivationStateMutationSchema.class)
@JsonPropertyOrder({
  "identifier", "emittionDate", "sensorIdentifier",
  "start", "end", "duration", "milliseconds",
  "nodeIdentifier"
})
public class ActivationState extends State
{  
  public static EntityFieldSelector<ActivationState, Expression<Long>> DURATION_SELECTOR = (query, queried) -> {
    final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
    final Expression<ZonedDateTime> start = queried.get(ActivationState_._start);
    final Expression<ZonedDateTime> end = queried.get(ActivationState_._end);
    
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
  
  @Column(name = "start", nullable = false, unique = false, updatable = true)
  private ZonedDateTime _start;

  @Column(name = "end", nullable = true, unique = false, updatable = true)
  private ZonedDateTime _end;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = true)
  private Node _node;
  
  public ActivationState () {
    _start = null;
    _end = null;
    _node = null;
  }
  
  public Duration getDuration () {
    if (_end == null) {
      return null;
    } else {
      return Duration.between(_start, _end);
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
    return _end;
  }
  
  public void setEnd (@NonNull final ZonedDateTime end) {
    _end = end;
  }

  public Node getNode () {
    return _node;
  }
  
  public Long getNodeIdentifier () {
    if (_node == null) return null;
    else return _node.getIdentifier();
  }

  public void setNodeIdentifier (@NonNull final Long node) {
    _node = EntityCollections.NODES.findByIdentifier(node).get();
  }

  public void setNode (@NonNull final Node node) {
    _node = node;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public void setStart (@NonNull final ZonedDateTime start) {
    _start = start;
  }
  
  @JsonIgnore
  public State getStartState () {
    return getCorrelation("start");
  }
  
  @JsonIgnore
  public Long getStartStateIdentifier () {
    return getCorrelation("start").getIdentifier();
  }
  
  @JsonIgnore
  public State getEndState () {
    if (_end == null) return null;
    
    return getCorrelation("end");
  }
  
  @JsonIgnore
  public Long getEndStateIdentifier () {
    if (_end == null) return null;
    
    return getCorrelation("end").getIdentifier();
  }

  public boolean contains (@NonNull final State state) {
    return contains(state.getEmittionDate());
  }
  
  public boolean contains (@NonNull final ZonedDateTime date) {
    if (getStart() == null && getEnd() == null) {
      return true;
    } else if (getStart() != null && getEnd() == null) {
      return date.compareTo(getStart()) >= 0;
    } else if (getStart() == null && getEnd() != null) {
      return date.compareTo(getEnd()) < 0;
    } else {
      return date.compareTo(getStart()) >= 0 && 
             date.compareTo(getEnd()) < 0;
    }
  }
  
  @Override
  public ActivationStateSnapshot snapshot () {
    return new ActivationStateSnapshot(this);
  }
  
  @Override
  public ApplicationEntityReference<? extends ActivationState> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
