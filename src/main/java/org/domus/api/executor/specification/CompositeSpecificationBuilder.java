package org.domus.api.executor.specification;

import java.util.ArrayList;
import java.util.List;

import org.domus.api.collection.specification.ConjunctionSpecification;
import org.domus.api.collection.specification.Specification;
import org.domus.api.executor.RequestError;
import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CompositeSpecificationBuilder<Entity> implements SpecificationBuilder<Entity>
{
  @NonNull
  private final List<SpecificationBuilder<Entity>> _builders;
  @Nullable
  private Specification<Entity>                    _result;

  public CompositeSpecificationBuilder() {
    this._builders = new ArrayList<>();
    this._result = null;
  }

  public List<SpecificationBuilder<Entity>> getBuilders () {
    return this._builders;
  }

  @Override
  public void execute (@NonNull final APIRequest request) {
    @SuppressWarnings("unchecked")
    final Specification<Entity>[] specs = this._builders.stream().map(item -> {
      item.execute(request);
      return item.getResult();
    }).toArray(size -> (Specification<Entity>[]) new Specification[this._builders.size()]);

    this._result = new ConjunctionSpecification<>(specs);
  }

  @Override
  public List<RequestError> validate (@NonNull final APIRequest request) {
    final List<RequestError> result = new ArrayList<>();

    this._builders.stream().map(item -> item.validate(request)).forEach(list -> result.addAll(list));

    return result;
  }

  @Override
  public Specification<Entity> getResult () {
    return this._result;
  }

}
