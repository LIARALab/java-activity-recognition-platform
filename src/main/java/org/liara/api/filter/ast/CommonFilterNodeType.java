package org.liara.api.filter.ast;

import org.springframework.lang.NonNull;

public enum CommonFilterNodeType implements FilterNodeType
{
  CONJUNCTION("conjunction"),
  DISJUNCTION("disjunction"),
  
  GREATHER_THAN("greather than"),
  GREATHER_THAN_OR_EQUAL_TO("greather than or equal to"),
  LESS_THAN("less than"),
  LESS_THAN_OR_EQUAL_TO("less than or equal to"),
  BETWEEN("between"),
  EQUAL_TO("equal to"),
  IN("in"),
  CONTAINS("contains"),
  
  NOT("not"),
  
  VALUE_INTEGER("value<Integer>"),
  VALUE_FLOAT("value<Float>"),
  VALUE_LONG("value<Long>"),
  VALUE_DOUBLE("value<Double>"),
  VALUE_STRING("value<String>"),
  VALUE_DATETIME("value<PartialLocalDateTime>"),
  VALUE_TIME("value<PartialLocalTime>");
  
  @NonNull
  private final String _name;
  
  private CommonFilterNodeType (@NonNull final String name) {
    _name = name;
  }

  @Override
  public String getName () {
    return _name;
  }
}
