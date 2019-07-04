package org.liara.api.data.series;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.LongValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LongSeriesBuilder
{
  @Nullable
  private Long _sensor;

  @Nullable
  private LongValueStateRepository _longValues;

  public LongSeriesBuilder () {
    _sensor = null;
    _longValues = null;
  }

  public LongSeriesBuilder (@NonNull final LongSeriesBuilder toCopy) {
    _sensor = toCopy.getSensor();
    _longValues = toCopy.getLongValues();
  }

  public @NonNull LongSeries build () {
    return new LongSeries(this);
  }

  public @Nullable Long getSensor () {
    return _sensor;
  }

  public void setSensor (@Nullable final Long sensor) {
    _sensor = sensor;
  }

  public @Nullable LongValueStateRepository getLongValues () {
    return _longValues;
  }

  @Autowired
  public void setLongValues (@Nullable final LongValueStateRepository longValues) {
    _longValues = longValues;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof LongSeriesBuilder) {
      @NonNull final LongSeriesBuilder otherLongSeriesBuilder = (LongSeriesBuilder) other;

      return (
        Objects.equals(
          _sensor,
          otherLongSeriesBuilder.getSensor()
        ) &&
        Objects.equals(
          _longValues,
          otherLongSeriesBuilder.getLongValues()
        )
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_sensor, _longValues);
  }
}
