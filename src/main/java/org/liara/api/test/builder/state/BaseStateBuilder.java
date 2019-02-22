package org.liara.api.test.builder.state;

import org.liara.api.data.entity.state.State;
import org.liara.api.test.builder.Builder;
import org.liara.api.test.builder.IdentityBuilder;
import org.liara.api.test.builder.entity.BaseApplicationEntityBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseStateBuilder<
                        Self extends BaseStateBuilder<Self, Entity>,
                        Entity extends State
                      >
                extends BaseApplicationEntityBuilder<Self, Entity>
{
  @NonNull
  private static final DateTimeFormatter DEFAULT_FORMATTER = new DateTimeFormatterBuilder().appendPattern(
    "d-M-y[ H:m:s.S]"
  ).parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
   .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
   .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
   .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
   .toFormatter();
  
  @Nullable
  private ZonedDateTime _emissionDate = null;
  
  @NonNull
  private final Map<String, Builder<?, ? extends State>> _correlations = new HashMap<>();
  
  public Self withEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emissionDate = emittionDate;
    
    return self();
  }
  
  public Self withEmittionDate (@NonNull final String string) {
    _emissionDate = ZonedDateTime.parse(
      string, 
      BaseStateBuilder.DEFAULT_FORMATTER
    );
    
    return self();
  }
  
  public Self withCorrelation (
    @NonNull final String name, 
    @NonNull final Builder<?, ? extends State> correlated
  ) {
    _correlations.put(name, correlated);
    return self();
  }
  
  public Self withCorrelation (
    @NonNull final String name, 
    @NonNull final State correlated
  ) {
    return withCorrelation(name, IdentityBuilder.of(correlated));
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emissionDate;
  }
  
  protected void apply (@NonNull final State state) {
    super.apply(state);
    state.setEmissionDate(_emissionDate);
    
    for (
      final Entry<
        String, Builder<?, ? extends State>
      > correlation : this._correlations.entrySet()
    ) {
      state.correlate(
        correlation.getKey(), 
        correlation.getValue().build()
      );
    }
  }
}
