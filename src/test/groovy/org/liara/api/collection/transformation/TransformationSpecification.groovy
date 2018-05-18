package org.liara.api.collection.transformation

import org.liara.api.collection.view.ValueView
import spock.lang.Specification

public class TransformationSpecification extends Specification
{
  def "it can be defined as a lambda" () {
    given: "a lambda transformation"
      final Transformation<ValueView<Number>, ValueView<Number>> transformation = {
        ValueView<Number> base -> ValueView.of(base.get() * 2)
      }
      
    when: "we apply the transformation to a valid view"
      final ValueView<Number> result = transformation.apply(ValueView.of(5))
      
    then: "we expect that the result view was transformed accordingly"
      result.get() == 10
  }
  
  def "it can be applied to another transformation in order to create a transformation chain" () {
    given: "two lambda transformation"
      final Transformation<ValueView<Number>, ValueView<Number>> powTransformation = {
        ValueView<Number> base -> ValueView.of(base.get() * 2)
      }
      
      final Transformation<ValueView<Number>, ValueView<String>> toStringTransformation = {
        ValueView<Number> base -> ValueView.of(String.valueOf(base.get()))
      }
      
    when: "we apply transformation to other transformation"
      final Transformation<ValueView<Number>, ValueView<String>> result = toStringTransformation.apply(
        powTransformation.apply(powTransformation)
      )
      
    then: "we expect the result transformation to transform a view the same way as a chain application of its child transformation"
      result.apply(ValueView.of(5)).get().equals("20") == true
      toStringTransformation.apply(
        powTransformation.apply(
          powTransformation.apply(
            ValueView.of(5)
          )
        )
      ).get().equals("20") == true
  }
}
