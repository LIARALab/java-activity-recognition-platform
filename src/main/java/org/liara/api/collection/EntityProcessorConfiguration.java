package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.request.processor.APIRequestProcessorParser;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;
import org.liara.selection.processor.ProcessorExecutor;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.util.*;

public class EntityProcessorConfiguration<Entity, Result extends Operator>
  extends EntityConfiguration<Entity>
{
  @NonNull
  private final String _field;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Result>> _processors;

  @Nullable
  private APIRequestProcessorParser<Result> _parser;

  public EntityProcessorConfiguration (
    @NonNull final String field, @NonNull final ManagedType<Entity> entity
  )
  {
    super(entity);
    _field = field;
    _processors = new HashMap<>();
    _parser = null;
  }

  public void setProcessor (
    @NonNull final Attribute<? super Entity, ?> attribute, @NonNull final ProcessorExecutor<Result> executor
  )
  {
    _processors.put(attribute, executor);
    _parser = null;
  }

  @Override
  public @NonNull APIRequestValidator getValidator (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    return new APIRequestProcessorParser<>(_field, _processors.get(attribute));
  }

  @Override
  public @NonNull APIRequestValidator getValidator (
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes
  )
  {
    @NonNull final List<@NonNull ProcessorExecutor<Result>> processors = new ArrayList<>(attributes.size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      processors.add(_processors.get(attribute));
    }

    return new APIRequestProcessorParser<>(_field, ProcessorExecutor.composition(processors));
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    if (_parser == null) createParser();
    return _parser;
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    return new APIRequestProcessorParser<>(_field, _processors.get(attribute));
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser (
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes
  )
  {
    @NonNull final List<@NonNull ProcessorExecutor<Result>> processors = new ArrayList<>(attributes.size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      processors.add(_processors.get(attribute));
    }

    return new APIRequestProcessorParser<>(_field, ProcessorExecutor.composition(processors));
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    if (_parser == null) createParser();
    return _parser;
  }

  private void createParser () {
    @NonNull final List<@NonNull ProcessorExecutor<Result>> processors = new ArrayList<>(_processors.size());
    processors.addAll(_processors.values());

    _parser = new APIRequestProcessorParser<>(_field, ProcessorExecutor.composition(processors));
  }
}
