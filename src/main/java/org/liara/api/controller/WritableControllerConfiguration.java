package org.liara.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.validation.InvalidModelException;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

public class WritableControllerConfiguration
  extends ReadableControllerConfiguration
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @NonNull
  private final ObjectMapper _objectMapper;

  @NonNull
  private final Validator _validator;

  public WritableControllerConfiguration (
    @NonNull final WritableControllerConfigurationBuilder builder
  )
  {
    super(builder);

    _applicationEventPublisher = Objects.requireNonNull(builder.getApplicationEventPublisher());
    _objectMapper = Objects.requireNonNull(builder.getObjectMapper());
    _validator = Objects.requireNonNull(builder.getValidator());
  }

  public @NonNull ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  public @NonNull ObjectMapper getObjectMapper () {
    return _objectMapper;
  }

  public @NonNull Validator getValidator () {
    return _validator;
  }

  public void assertIsValid (@NonNull final Object object)
  throws InvalidModelException
  {
    @NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> errors = _validator.validate(object);

    if (errors.size() > 0) {
      throw new InvalidModelException(errors);
    }
  }

  @Override
  public int hashCode () {
    return Objects.hash(super.hashCode(), _applicationEventPublisher, _objectMapper, _validator);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (!super.equals(other)) return false;

    if (other instanceof WritableControllerConfiguration) {
      @NonNull final WritableControllerConfiguration otherConfiguration = (WritableControllerConfiguration) other;

      return Objects.equals(_applicationEventPublisher, otherConfiguration.getApplicationEventPublisher()) &&
             Objects.equals(_objectMapper, otherConfiguration.getObjectMapper()) && Objects.equals(
        _validator,
        otherConfiguration.getValidator()
      );
    }

    return false;
  }
}
