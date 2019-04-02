package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelStateRepository;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseLabelStateRepository
  extends DatabaseStateRepository<LabelState>
  implements LabelStateRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseLabelStateRepository (@NonNull final EntityManager entityManager) {
    super(entityManager, LabelState.class);

    _entityManager = entityManager;
  }

  @Override
  public @NonNull Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area,
    @NonNull final Long sensorIdentifier
  ) {
    @NonNull final TypedQuery<LabelState> query = _entityManager.createQuery(
      "SELECT label FROM " + getManagedEntity().getName() + " label" +
      " WHERE label.start <= :area " +
      "   AND (label.end IS NULL OR label.end >= :area)" +
      "   AND label.sensorIdentifier = :sensorIdentifier" +
      " ORDER BY label.start ASC, label.identifier ASC",
      LabelState.class
    );

    query.setParameter("area", area);
    query.setParameter("sensorIdentifier", sensorIdentifier);
    query.setMaxResults(1);
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    @NonNull final List<@NonNull LabelState> result = query.getResultList();

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }
}
