package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestParameterMapConfiguration
  implements RequestConfiguration
{
  @NonNull
  private final MapConfiguration<RequestParameterConfiguration> _configuration;

  public RequestParameterMapConfiguration () {
    _configuration = new SimpleMapConfiguration<>();
  }

  public RequestParameterMapConfiguration (
    @NonNull final MapConfiguration<RequestParameterConfiguration> configuration
  )
  {
    _configuration = configuration;
  }

  public RequestParameterMapConfiguration (
    @NonNull final RequestParameterMapConfiguration toCopy
  )
  {
    _configuration = Duplicator.duplicate(toCopy.getConfiguration());
  }

  public @NonNull MapConfiguration<RequestParameterConfiguration> getConfiguration () {
    return _configuration;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return getValidator(_configuration);
  }

  private @NonNull APIRequestValidator getValidator (
    @NonNull final MapConfiguration<RequestParameterConfiguration> map
  )
  {
    @NonNull final List<@NonNull APIRequestValidator> validators = new ArrayList<>();

    validators.add(APIRequestValidator.all(map.getFieldParameters()
                                             .stream()
                                             .map((@NonNull final String field) -> APIRequestValidator.field(
                                               field,
                                               map.getFieldConfiguration(field).orElseThrow().getValidator()
                                             ))
                                             .collect(Collectors.toList())));

    map.getRequestParameters().stream().map((@NonNull final String field) -> APIRequestValidator.childRequest(
      field,
      getValidator(map.getRequestConfiguration(field).orElseThrow())
    )).forEach(validators::add);

    return APIRequestValidator.all(validators);
  }

  @Override
  public @NonNull APIRequestParser<@NonNull Operator> getParser () {
    return getParser(_configuration).orElse(Identity.INSTANCE);
  }

  private @NonNull APIRequestParser<Operator> getParser (
    @NonNull final MapConfiguration<RequestParameterConfiguration> map
  )
  {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>();

    parsers.add(APIRequestParser.all(map.getFieldParameters()
                                       .stream()
                                       .map((@NonNull final String field) -> APIRequestParser.field(
                                         field,
                                         map.getFieldConfiguration(field).orElseThrow().getParser()
                                       ).mapNonNull(Composition::of))
                                       .collect(Collectors.toList())).mapNonNull(Composition::of));

    map.getRequestParameters().stream().map((@NonNull final String field) -> APIRequestParser.childRequest(
      field,
      getParser(map.getRequestConfiguration(field).orElseThrow())
    )).forEach(parsers::add);

    return APIRequestParser.all(parsers).mapNonNull(Composition::of);
  }
}
