package org.liara.api.date;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.springframework.lang.NonNull;

public class PartialZonedDateTime implements TemporalAccessor, Comparable<TemporalAccessor>
{  
  @NonNull public static final ChronoField[] DATETIME_FIELDS = new ChronoField[] {
    ChronoField.YEAR,
    ChronoField.MONTH_OF_YEAR,
    ChronoField.DAY_OF_MONTH,
    ChronoField.HOUR_OF_DAY,
    ChronoField.MINUTE_OF_HOUR,
    ChronoField.SECOND_OF_MINUTE,
    ChronoField.MICRO_OF_SECOND
  };

  @NonNull public static final ChronoField[] CONTEXT_FIELDS = new ChronoField[] {
    ChronoField.ALIGNED_WEEK_OF_YEAR,
    ChronoField.ALIGNED_WEEK_OF_MONTH,
    ChronoField.DAY_OF_YEAR,
    ChronoField.DAY_OF_WEEK
  };
 
  @NonNull public static final ChronoField[] COMPARISON_FIELDS = new ChronoField[] {
    ChronoField.YEAR,
    ChronoField.MONTH_OF_YEAR,
    ChronoField.ALIGNED_WEEK_OF_YEAR,
    ChronoField.ALIGNED_WEEK_OF_MONTH,
    ChronoField.DAY_OF_YEAR,
    ChronoField.DAY_OF_MONTH,
    ChronoField.DAY_OF_WEEK,
    ChronoField.HOUR_OF_DAY,
    ChronoField.MINUTE_OF_HOUR,
    ChronoField.SECOND_OF_MINUTE,
    ChronoField.MICRO_OF_SECOND
  };
  
  @NonNull private final TemporalAccessor _date;
  
  public static PartialZonedDateTime from (@NonNull final TemporalAccessor date) {
    return new PartialZonedDateTime(date);
  }
  
  protected PartialZonedDateTime (@NonNull final TemporalAccessor date) {
    _date = date;
  }
  
  public static Expression<Long> select (
    @NonNull final Expression<ZonedDateTime> _path, 
    @NonNull final CriteriaBuilder builder,
    @NonNull final ChronoField field
  ) {
    switch (field) {
      case MICRO_OF_SECOND:
        return builder.function("MICROSECOND", Long.class, _path);
      case SECOND_OF_MINUTE:
        return builder.function("SECOND", Long.class, _path);
      case MINUTE_OF_HOUR:
        return builder.function("MINUTE", Long.class, _path);
      case HOUR_OF_DAY:
        return builder.function("HOUR", Long.class, _path);
      case DAY_OF_YEAR:
        return builder.function("DAYOFYEAR", Long.class, _path);
      case DAY_OF_MONTH:
        return builder.function("DAYOFMONTH", Long.class, _path);
      case DAY_OF_WEEK:
        return builder.function("DAYOFWEEK", Long.class, _path);
      case ALIGNED_WEEK_OF_YEAR:
        return builder.function("WEEKOFYEAR", Long.class, _path);
      case ALIGNED_WEEK_OF_MONTH:
        return builder.function("WEEK", Long.class, _path);
      case MONTH_OF_YEAR:
        return builder.function("MONTH", Long.class, _path);
      case YEAR:
        return builder.function("YEAR", Long.class, _path);
      default:
        throw new Error("Unhandled field " + field);
    }
  }
  

  public Expression<ZonedDateTime> mask (
    @NonNull final Expression<ZonedDateTime> _path, 
    @NonNull final CriteriaBuilder builder
  ) {
    return builder.function("STR_TO_DATE",
      ZonedDateTime.class, 
      builder.function(
        "DATE_FORMAT", 
        String.class, 
        _path,
        builder.literal(this.getSQLMask())
      ),
      builder.literal("%Y-%m-%d %H:%i:%s.%f")
    );
  }

  @Override
  public int compareTo (@NonNull final TemporalAccessor other) {
    for (final ChronoField field : COMPARISON_FIELDS) {
      if (isSupported(field) && other.isSupported(field)) {
        final long myValue = this.getLong(field);
        final long otherValue = other.getLong(field);
        
        if (myValue < otherValue) {
          return -1;
        } else if (myValue > otherValue) {
          return 1;
        }
      }
    }
    
    return 0;
  }
  
  public ZonedDateTime toZonedDateTime () {
    ZonedDateTime result = ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    
    for (final ChronoField field : new ChronoField[] {
      ChronoField.YEAR,
      ChronoField.MONTH_OF_YEAR,
      ChronoField.DAY_OF_MONTH,
      ChronoField.HOUR_OF_DAY,
      ChronoField.MINUTE_OF_HOUR,
      ChronoField.SECOND_OF_MINUTE,
      ChronoField.MICRO_OF_SECOND
    }) {
      if (isSupported(field)) {
        result = result.with(field, getLong(field));
      }
    }
    
    return result;
  }
  
  public boolean isCompleteZonedDateTime () {
    for (final ChronoField field : COMPARISON_FIELDS) {
      if (!isSupported(field)) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public long getLong (@NonNull final TemporalField field) {
    return this._date.getLong(field);
  }

  @Override
  public boolean isSupported (@NonNull final TemporalField field) {
    return this._date.isSupported(field);
  }
  
  public String getSQLMask () {
    final StringBuilder builder = new StringBuilder();
    
    builder.append((isSupported(ChronoField.YEAR)) ? "%Y" : "1900");
    builder.append("-");
    builder.append((isSupported(ChronoField.MONTH_OF_YEAR)) ? "%m" : "01");
    builder.append("-");
    builder.append((isSupported(ChronoField.DAY_OF_MONTH)) ? "%d" : "01");
    builder.append(" ");
    builder.append((isSupported(ChronoField.HOUR_OF_DAY)) ? "%H" : "00");
    builder.append(":");
    builder.append((isSupported(ChronoField.MINUTE_OF_HOUR)) ? "%i" : "00");
    builder.append(":");
    builder.append((isSupported(ChronoField.SECOND_OF_MINUTE)) ? "%s" : "00");
    builder.append(".");
    builder.append((isSupported(ChronoField.MICRO_OF_SECOND)) ? "%f" : "000000");
    
    return builder.toString();
  }
  
  public boolean containsDatetime () {
    for (final ChronoField field : PartialZonedDateTime.DATETIME_FIELDS) {
      if (isSupported(field)) return true;
    }
    
    return false;
  }
  
  public boolean containsContext () {
    for (final ChronoField field : PartialZonedDateTime.CONTEXT_FIELDS) {
      if (isSupported(field)) return true;
    }
    
    return false;
  }

  @Override
  public String toString () {
    final StringBuilder builder = new StringBuilder();
    
    builder.append((isSupported(ChronoField.YEAR)) ? getLong(ChronoField.YEAR) : "****");
    builder.append("-");
    builder.append((isSupported(ChronoField.MONTH_OF_YEAR)) ? getLong(ChronoField.MONTH_OF_YEAR) : "**");
    builder.append("-");
    builder.append((isSupported(ChronoField.DAY_OF_MONTH)) ? getLong(ChronoField.DAY_OF_MONTH) : "**");
    builder.append("T");
    builder.append((isSupported(ChronoField.HOUR_OF_DAY)) ? getLong(ChronoField.HOUR_OF_DAY) : "**");
    builder.append(":");
    builder.append((isSupported(ChronoField.MINUTE_OF_HOUR)) ? getLong(ChronoField.MINUTE_OF_HOUR) : "**");
    builder.append(":");
    builder.append((isSupported(ChronoField.SECOND_OF_MINUTE)) ? getLong(ChronoField.SECOND_OF_MINUTE) : "**");
    builder.append(".");
    builder.append((isSupported(ChronoField.MICRO_OF_SECOND)) ? getLong(ChronoField.MICRO_OF_SECOND) : "******");
    
    return builder.toString();
  }
}
