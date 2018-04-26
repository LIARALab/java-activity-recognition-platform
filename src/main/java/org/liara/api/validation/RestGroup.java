package org.liara.api.validation;

public final class RestGroup
{
  public static interface Any { }
  
  public static interface Post extends Any {}
  public static interface Put extends Any {}
  public static interface Patch extends Any {}
  public static interface Update extends Any {}
  public static interface Delete extends Any {}
  public static interface Get extends Any {}
  public static interface Head extends Any {}
  public static interface Option extends Any {}
  
  public static interface EntityCreation extends Post, Put { }
  public static interface EntityMutation extends Patch, Update { }
  public static interface EntityDeletion extends Delete { }
  public static interface EntityReading extends Get { }
}
