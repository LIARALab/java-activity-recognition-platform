package org.liara.api.test.builder;

public interface Builder<Self extends Builder<Self, Result>, Result>
{
  public Result build ();
  public Self self ();
}
