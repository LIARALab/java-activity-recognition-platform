package org.liara.api.data.operators;

import com.google.common.collect.Iterators;
import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class StateOperators
{
  public static <T extends State> EntityCollectionOperator<T> after (
    @NonNull final ZonedDateTime date
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.greaterThan(query.getEntity().get("_emissionDate"), date)
      );
    };
  }
  
  public static <T extends State> EntityCollectionOperator<T> afterOrAt (
    @NonNull final ZonedDateTime date
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.greaterThanOrEqualTo(query.getEntity().get("_emissionDate"), date)
      );
    };
  }
  
  public static <T extends State> EntityCollectionOperator<T> before (
    @NonNull final ZonedDateTime date
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.lessThan(query.getEntity().get("_emissionDate"), date)
      );
    };
  }
  
  public static <T extends State> EntityCollectionOperator<T> beforeOrAt (
    @NonNull final ZonedDateTime date
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.lessThanOrEqualTo(query.getEntity().get("_emissionDate"), date)
      );
    };
  }
  
  public static <T extends State> EntityCollectionOperator<T> of (
    @NonNull final Sensor sensor
  ) {
    return query -> query.andWhere(query.getEntity().get("_sensor").in(sensor));
  }
  
  public static <T extends State> EntityCollectionOperator<T> of (
    @NonNull final Sensor[] sensors
  ) {
    return query -> query.andWhere(
      query.getEntity().get("_sensor").in(Arrays.asList(sensors))
    );
  }
  
  public static <T extends State> EntityCollectionOperator<T> of (
    @NonNull final Collection<Sensor> sensors
  ) {
    return query -> query.andWhere(
      query.getEntity().get("_sensor").in(sensors)
    );
  }
  
  public static <T extends State> EntityCollectionOperator<T> of (
    @NonNull final Iterator<Sensor> sensors
  ) {
    return query -> query.andWhere(
      query.getEntity().get("_sensor").in(
        Arrays.asList(Iterators.toArray(sensors, Sensor.class))
      )
    );
  }
  
  public static <T extends State> EntityCollectionOperator<T> of (
    @NonNull final EntityCollection<Sensor> sensors
  ) {
    return query -> sensors.getOperator().apply(query.join(entity -> entity.join("_sensor")));
  }
}
