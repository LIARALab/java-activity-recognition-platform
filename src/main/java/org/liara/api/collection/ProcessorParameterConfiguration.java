package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.request.processor.APIRequestProcessorParser;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.validator.APIRequestFieldValidator;
import org.liara.selection.processor.Processor;
import org.liara.selection.processor.ProcessorExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProcessorParameterConfiguration
  implements RequestParameterConfiguration
{
  @NonNull
  private final MapConfiguration<Processor<Operator>> _configuration;

  @Nullable
  private APIRequestProcessorParser<Operator> _result = null;

  public ProcessorParameterConfiguration () {
    _configuration = new SimpleMapConfiguration<>();
  }

  public ProcessorParameterConfiguration (
    @NonNull final MapConfiguration<Processor<Operator>> configuration
  )
  {
    _configuration = configuration;
  }

  public ProcessorParameterConfiguration (
    @NonNull final ProcessorParameterConfiguration toCopy
  )
  {
    _configuration = Duplicator.duplicate(toCopy.getConfiguration());
    _result = null;
  }

  public @NonNull MapConfiguration<Processor<Operator>> getConfiguration () {
    return _configuration;
  }

  @Override
  public @NonNull APIRequestFieldValidator getValidator () {
    if (_result == null) build();
    return _result;
  }

  @Override
  public @NonNull APIRequestFieldParser<@NonNull Operator> getParser () {
    if (_result == null) build();
    return _result;
  }

  private void build () {
    @NonNull final Set<@NonNull RequestPath> parameters = _configuration.getDeepParameters();
    @NonNull final Map<@NonNull String, @NonNull ProcessorExecutor<Operator>> executors =
      new HashMap<>(parameters.size());

    for (@NonNull final RequestPath path : parameters) {
      executors.put(path.toString(),
        ProcessorExecutor.execute(_configuration.getFieldConfiguration(path).orElseThrow())
      );
    }

    _result = new APIRequestProcessorParser<>(ProcessorExecutor.map(executors));
  }
}
