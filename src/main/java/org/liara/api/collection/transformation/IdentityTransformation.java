package org.liara.api.collection.transformation;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

public class      IdentityTransformation<Collection extends View<?>>
       implements Transformation<Collection, Collection>
{
  @Override
  public Collection apply (@NonNull final Collection input) {
    return input;
  }
}
