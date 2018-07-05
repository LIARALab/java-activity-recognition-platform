package org.liara.api.data.entity.state;

import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "states_activity")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(ActivityStateCreationSchema.class)
@UseMutationSchema(ActivityStateMutationSchema.class)
public class ActivityState extends State
{
  public static EntityFieldSelector<ActivityState, Expression<Long>> DURATION_SELECTOR = (query, queried) -> {
    final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
    final Expression<ZonedDateTime> start = queried.get("_start");
    final Expression<ZonedDateTime> end = queried.get("_end");
    
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
  
  @Column(name = "tag", nullable = false, updatable = true, unique = false)
  private String _tag;

  @Column(name = "start", nullable = false, updatable = true, unique = false)
  private ZonedDateTime _start;

  @Column(name = "end", nullable = true, updatable = true, unique = false)
  private ZonedDateTime _end;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = true)
  private Node _node;

  public ActivityState () { 
    super();
  }
  
  public ActivityState (
    @NonNull final EntityManager manager,
    @NonNull final ActivityStateCreationSchema schema
  ) {
    super (manager, schema);
    
    _tag = schema.getTag();
    _start = schema.getStart();
    _end = schema.getEnd();
    _node = manager.find(Node.class, schema.getNode());
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
  
  public String getTag () {
    return _tag;
  }

  public void setTag (@NonNull final String tag) {
    _tag = tag;
  }

  public ZonedDateTime getStart () {
    return _start;
  }

  public void setStart (@NonNull final ZonedDateTime start) {
    _start = start;
  }

  public ZonedDateTime getEnd () {
    return _end;
  }

  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }

  public Long getNodeIdentifier () {
    return _node.getIdentifier();
  }

  public void setNodeIdentifier (@NonNull final Long identifier) {
    _node = EntityCollections.NODES.findByIdentifier(identifier).get();
  }
  
  @JsonIgnore
  public Node getNode () {
    return _node;
  }

  public void setNode (@NonNull final Node node) {
   _node = node;
  }
  
  @Override
  public ActivityStateSnapshot snapshot () {
    return new ActivityStateSnapshot(this);
  }
}
