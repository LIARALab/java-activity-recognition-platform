package org.liara.api.collection.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class      TransformationConjunction<
                    Input extends View<?>
                  >
       implements Transformation<Input, Input>
{
  @NonNull
  private final List<Transformation<Input, Input>> _transformations = new ArrayList<>();
  
  public TransformationConjunction (
    @NonNull final Iterable<Transformation<Input, Input>> transformations
  ) {
    Iterables.addAll(_transformations, transformations);
  }
  
  public TransformationConjunction (
    @NonNull final Iterator<Transformation<Input, Input>> transformations
  ) {
    Iterators.addAll(_transformations, transformations);
  }
  
  public TransformationConjunction (
    @NonNull final Transformation<Input, Input>[] transformations
  ) {
    _transformations.addAll(Arrays.asList(transformations));
  }

  @Override
  public Input apply (@NonNull final Input collection) {
    Input result = collection;
    
    for (final Transformation<Input, Input> transformation : _transformations) {
      result = transformation.apply(result);
    }
    
    return result;
  }
}
