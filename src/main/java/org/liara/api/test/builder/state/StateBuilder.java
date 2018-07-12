package org.liara.api.test.builder.state;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.liara.api.data.entity.state.State;
import org.liara.api.test.builder.entity.BaseEntityBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class StateBuilder<Self extends StateBuilder<Self>>
                extends BaseEntityBuilder<Self>
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
  private ZonedDateTime _emittionDate = null;
  
  public Self withEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
    
    return self();
  }
  
  public Self withEmittionDate (@NonNull final String string) {
    _emittionDate = ZonedDateTime.parse(
      string, 
      StateBuilder.DEFAULT_FORMATTER
    );
    
    return self();
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  public State build () {
    final State result = new State();
    
    apply(result);
    
    return result;
  }
  
  protected void apply (@NonNull final State state) {
    super.apply(state);
    state.setEmittionDate(_emittionDate);
  }
}
