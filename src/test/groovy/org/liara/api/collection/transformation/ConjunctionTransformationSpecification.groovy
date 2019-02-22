package org.liara.api.collection.transformation

import org.liara.api.collection.view.View
import org.mockito.Mockito
import spock.lang.Specification

class ConjunctionTransformationSpecification
  extends Specification
{
  def "applied to a view, it delegate the operation in a valid order, to its conjuged transformation" () {
    given: "a view to transform"
      final View<Number> view = Mockito.mock(View.class)
      
    and: "two transformation"
      final View<Number> intermediateView = Mockito.mock(View.class)
      final View<String> resultView = Mockito.mock(View.class)
      final Transformation<View<Number>, View<Number>> first = Mockito.mock(Transformation.class)
      final Transformation<View<Number>, View<String>> second = Mockito.mock(Transformation.class)
      Mockito.when(first.apply(view)).thenReturn(intermediateView)
      Mockito.when(second.apply(intermediateView)).thenReturn(resultView)
      
    and: "a transformation that is the conjunction of the two given transformation"
      final Transformation<View<Number>, View<String>> conjugation = ConjunctionTransformation.conjugate(
        second, first
      )
      
    when: "we apply the transformation to a view"
      final View<String> result = conjugation.apply(view)
      
    then: "we expect that the conjugation was correctly executed"
      result == resultView
  }
}
