package org.liara.api.data.series;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SeriesManager
{
  @NonNull
  private final Map<@NonNull Long, @NonNull Series> _series;

  @NonNull
  private final LongSeriesBuilder _longSeriesBuilder;

  @Autowired
  public SeriesManager (@NonNull final ApplicationContext context) {
    _longSeriesBuilder = context.getBean(LongSeriesBuilder.class);
    _series = new HashMap<>();
  }

  public @NonNull LongSeries getLongSeries (@NonNull final Long sensor) {
    if (!_series.containsKey(sensor)) {
      _longSeriesBuilder.setSensor(sensor);
      _series.put(sensor, _longSeriesBuilder.build());
    }

    return (LongSeries) _series.get(sensor);
  }

  public void forget () {
    for (@NonNull final Series series : _series.values()) {
      series.forget();
    }
  }

  public void forget (@NonNull final Long sensor) {
    if (_series.containsKey(sensor)) _series.get(sensor).forget();
  }
}
