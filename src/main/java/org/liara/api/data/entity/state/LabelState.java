package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.time.Duration;
import java.time.ZonedDateTime;

@Entity
@Table(name = "states_label")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(LabelStateCreationSchema.class)
@UseMutationSchema(LabelStateMutationSchema.class)
@JsonPropertyOrder({
  "identifier", "emittionDate", "sensorIdentifier",
  "start", "end", "duration", "milliseconds"
})
public class LabelState
  extends State
{
  public static EntityFieldSelector<LabelState, Expression<Long>> DURATION_SELECTOR = (query, queried) -> {
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

  public static EntityFieldSelector<LabelState, Expression<String>> NIGHTS_SELECTOR = (query, queried) -> {
    final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();

    final Expression<String> lowerBound = builder.function("DATE_FORMAT",
      String.class,
      builder.function("DATE_SUB_HOUR", String.class, queried.get("_start"), builder.literal(12)),
      builder.literal("%Y-%m-%d")
    );

    final Expression<String> upperBound = builder.function("DATE_FORMAT",
      String.class,
      builder.function("DATE_ADD_HOUR", String.class, queried.get("_start"), builder.literal(12)),
      builder.literal("%Y-%m-%d")
    );

    return builder.concat(builder.concat(lowerBound, ":"), upperBound);
  };
  
  @Column(name = "tag", nullable = false, updatable = true, unique = false)
  private String _tag;

  @Column(name = "start", nullable = false, updatable = true, unique = false, precision = 6)
  private ZonedDateTime _start;

  @Column(name = "end", nullable = true, updatable = true, unique = false, precision = 6)
  private ZonedDateTime _end;

  public LabelState () {
    super();
    _tag = null;
    _start = null;
    _end = null;
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
      return getDuration().getSeconds() * 1_000L + getDuration().getNano() / 1_000_000L;
    }
  }
  
  public String getTag () {
    return _tag;
  }

  public void setTag (@NonNull final String tag) {
    _tag = tag;
  }

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  public ZonedDateTime getStart () {
    return _start;
  }

  public void setStart (@NonNull final ZonedDateTime start) {
    _start = start;
  }

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  public ZonedDateTime getEnd () {
    return _end;
  }

  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }

  public boolean contains (@NonNull final State state) {
    return contains(state.getEmissionDate());
  }

  public boolean contains (@NonNull final ZonedDateTime date) {
    if (getStart() == null && getEnd() == null) {
      return true;
    } else if (getStart() != null && getEnd() == null) {
      return date.compareTo(getStart()) >= 0;
    } else if (getStart() == null && getEnd() != null) {
      return date.compareTo(getEnd()) < 0;
    } else {
      return date.compareTo(getStart()) >= 0 && date.compareTo(getEnd()) < 0;
    }
  }

  @Override
  public LabelStateSnapshot snapshot () {
    return new LabelStateSnapshot(this);
  }
  
  @Override
  public ApplicationEntityReference<? extends LabelState> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
