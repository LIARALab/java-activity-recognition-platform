package org.liara.api.filter.validator;

import java.util.List;

import org.springframework.lang.NonNull;

public interface FilterValidator
{
  public List<String> validate (@NonNull final String filter);
}
