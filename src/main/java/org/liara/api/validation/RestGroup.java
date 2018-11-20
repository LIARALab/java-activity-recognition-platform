package org.liara.api.validation;

public final class RestGroup
{
  public interface Any {}

  public interface Post
    extends Any {}

  public interface Put
    extends Any {}

  public interface Patch
    extends Any {}

  public interface Update
    extends Any {}

  public interface Delete
    extends Any {}

  public interface Get
    extends Any {}

  public interface Head
    extends Any {}

  public interface Option
    extends Any {}

  public interface EntityCreation
    extends Post,
            Put {}

  public interface EntityMutation
    extends Patch,
            Update {}

  public interface EntityDeletion
    extends Delete {}

  public interface EntityReading
    extends Get {}
}
