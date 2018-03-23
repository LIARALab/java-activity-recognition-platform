package org.domus.api.collection.filter;

import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

public class IntegerColumnFilter<T> implements Filter<T> {
  @NonNull public static final Pattern NUMBER_PATTERN = Pattern.compile("((\\+|\\-)?\\d+)");
  @NonNull public static final Pattern PREDICATE_PATTERN = Pattern.compile(
    String.join(
      "|",
      "(<greaterThan>\\>\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<greaterThanOrEqual>\\>\\=\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<lessThan>\\<\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<lessThanOrEqual>\\<\\=\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<between>", NUMBER_PATTERN.pattern(), "\\s*\\-\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<equal>", NUMBER_PATTERN.pattern(), ")",
      "(<notBetween>\\!\\s*", NUMBER_PATTERN.pattern(), "\\s*\\-\\s*", NUMBER_PATTERN.pattern(), ")",
      "(<notEqual>\\!\\s*", NUMBER_PATTERN.pattern(), ")"
    )
  );
  
  @NonNull public static final Pattern CONJUNCTION_PATTERN = Pattern.compile(
    String.join(
      "",
      "(", PREDICATE_PATTERN.pattern(), ")",
      "(\\s+", PREDICATE_PATTERN.pattern(), ")*"
    )
  );
  
  @NonNull public static final Pattern FILTER_PATTERN = Pattern.compile(
      String.join(
        "",
        "^\\s*",
        "(", CONJUNCTION_PATTERN.pattern(), ")",
        "(\\s*,\\s*", CONJUNCTION_PATTERN.pattern(), ")*",
        "\\s*$"
      )
    );
  
  
  @NonNull private final String _column;
  @NonNull private final String _filter;

  public IntegerColumnFilter (
      @NonNull final String column,
      @NonNull final String filter
  ) {
    this._column = column;
    this._filter = filter;
  }
  
  /**
   * @see Filter#filter
   */
  public void filter (
      @NonNull final Root<T> root,
      @NonNull final CriteriaQuery<T> value, 
      @NonNull final CriteriaBuilder builder
  ) {    
  }
}
