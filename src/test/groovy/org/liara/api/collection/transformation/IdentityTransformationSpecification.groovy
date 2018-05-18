package org.liara.api.collection.transformation

import org.mockito.Mockito

import org.liara.api.collection.view.ValueView
import org.liara.api.collection.view.View
import spock.lang.Specification

public class IdentityTransformationSpecification extends Specification
{
  def "it return its given view instance when it is applied to a view instance" () {
    given: "an identity transformation"
      final IdentityTransformation<View<?>> identity = IdentityTransformation.instance()
      
    and: "a view instance"
      final View<?> view = Mockito.mock(View.class)
      
    when: "we apply the transformation to the view"
      final View<?> result = identity.apply(view)
      
    then: "we expect that the result of the transformation is the original view"
      result == view
  }
  
  def "it return the given transformation, when the identity is applied to another transformation" () {
    given: "an identity transformation"
      final IdentityTransformation<View<?>> identity = IdentityTransformation.instance()
      
    and: "another transformation instance"
      final Transformation<View<?>, View<?>> transformation = Mockito.mock(Transformation.class)
      
    when: "we apply the identity transformation to the other transformation"
      final Transformation<View<?>, View<?>> result = identity.apply(transformation)
      
    then: "we expect that the result of the application is the original transformation"
      result == transformation
  }
}
