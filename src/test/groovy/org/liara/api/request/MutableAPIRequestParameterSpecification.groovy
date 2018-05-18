package org.liara.api.request

import org.mockito.Mockito

import spock.lang.Specification

class MutableAPIRequestParameterSpecification extends Specification
{
  def "it can be instanciated with a given name" () {
    when: "we create a new instance of MutableAPIRequestParameter with a given name"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
    
    then: "we expect to get a valid instance of MutableAPIRequestParameter with the given name"
      parameter.name.equals("parameter") == true
      parameter.request == null
      parameter.valueCount == 0
  }
  
  def "it can be attached to another request" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    and: "a valid MutableAPIRequest"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      
    when: "we update the parameter parent's request"
      parameter.setRequest(request)
      
    then: "we expect that the request of the parameter has changed"
      parameter.request == request
      
    and: "we expect the parameter to register itself on the new request"
      Mockito.verify(request, Mockito.times(1)).addParameter(parameter)
  }
  
  def "it can be moved from a request to another" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    and: "a first MutableAPIRequest with the given parameter registered in"
      final MutableAPIRequest firstRequest = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(firstRequest)
      Mockito.reset(firstRequest)
      
    and: "a second MutableAPIRequest"
      final MutableAPIRequest secondRequest = Mockito.mock(MutableAPIRequest.class)
      
    when: "switch the parameter from a request to another"
      parameter.setRequest(secondRequest)
      
    then: "we expect that the request of the parameter has changed"
      parameter.request == secondRequest
      
    and: "we expect the parameter to register itself on the new request"
      Mockito.verify(secondRequest, Mockito.times(1)).addParameter(parameter)
      
    and: "we expect the parameter to unregister itself from the old request"
      Mockito.verify(firstRequest, Mockito.times(1)).removeParameter(parameter)
  }
  
  def "it can be detached from its current request" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    and: "a valid MutableAPIRequest with the given parameter registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(request)
      Mockito.reset(request)
      
    when: "we detach the parameter from its current request"
      parameter.setRequest(null)
      
    then: "we expect that the request of the parameter has changed"
      parameter.request == null
            
    and: "we expect the parameter to unregister itself from the old request"
      Mockito.verify(request, Mockito.times(1)).removeParameter(parameter)
  }
  
  def "it does nothing if you trying to change the parameter request by its current request" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
    
    and: "a valid MutableAPIRequest with the given parameter registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(request)
      Mockito.reset(request)
      
    when: "we try to move the parameter from its request, to its request"
      parameter.setRequest(parameter.request)
      
    then: "we expect that the parameter do not does anything"
      Mockito.verifyZeroInteractions(request)
  }
  
  def "it throw an IllegalArgumentException if you trying to set the request of the parameter to a request that already have a parameter with the same name" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    and: "a request with a parameter of the same name registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      Mockito.when(request.contains("parameter")).thenReturn(true)
      
    when: "we trying to set the parameter's request to the given request"
      parameter.setRequest(request)
      
    then: "we expect that the parameter throw an IllegalArgumentException"
      thrown IllegalArgumentException.class
  }
  
  def "it can be renamed" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("oldName")
     
    when: "we rename the parameter"
      parameter.setName("newName")
      
    then: "we expect that the parameter name has changed"
      parameter.name.equals("newName") == true
  }
  
  def "it propagate its name change to its parent request" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("oldName")
    
    and: "a MutableAPIRequest that has the given parameter registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(request)
      Mockito.reset(request)
      
    when: "we rename the parameter"
      parameter.setName("newName")
      
    then: "we expect the parameter request to be unchanged"
      parameter.request == request
      
    and: "we expect the parameter to unregister itself from the request and then re-register itself"
      def inOrder = Mockito.inOrder(request)
      inOrder.verify(request, Mockito.times(1)).removeParameter(parameter)
      inOrder.verify(request, Mockito.times(1)).addParameter(parameter)
  }
  
  def "it does nothing if you trying to change the parameter name by its current name" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("oldName")
    
    and: "a MutableAPIRequest that has the given parameter registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(request)
      Mockito.reset(request)
      
    when: "we rename the parameter with its old name"
      parameter.setName("oldName")
      
    then: "we expect the parameter to do not does anything"
      Mockito.verifyZeroInteractions(request)
  }
  
  def "it throw an IllegalArgumentException when you trying to rename a parameter of a request by using the name of another parameter of the same request" () {
    given: "a valid MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("oldName")
    
    and: "a MutableAPIRequest that has the given parameter, and another one registered in"
      final MutableAPIRequest request = Mockito.mock(MutableAPIRequest.class)
      parameter.setRequest(request)
      Mockito.reset(request)
      Mockito.when(request.contains("newName")).thenReturn(true)
      
    when: "we rename the parameter by using the name of the other request parameter"
      parameter.setName("newName")
      
    then: "we expect the parameter to throw an IllegalArgumentException"
      thrown IllegalArgumentException.class
  }
  
  def "it is possible to register a new String value into the parameter" () {
    given: "a valid empty MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    when: "we add some values to the parameter"
      parameter.addValue("first")
      parameter.addValue("second")
      parameter.addValue("third")
      
    then: "we expect that the parameter keep the value registered in"
      parameter.valueCount == 3
      Arrays.equals(parameter.getValues(), ["first", "second", "third"] as String[]) == true
  }
  
  def "it is possible to register a arrays of values into the parameter" () {
    given: "a valid empty MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    when: "we add some arrays of values to the parameter"
      parameter.addValues(["first", "second"] as String[])
      parameter.addValues(["third"] as String[])
      
    then: "we expect that the parameter keep the value registered in"
      parameter.valueCount == 3
      Arrays.equals(parameter.getValues(), ["first", "second", "third"] as String[]) == true
  }
  
  def "it is possible to register an iterable of values into the parameter" () {
    given: "a valid empty MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    when: "we add some iterable of values to the parameter"
      parameter.addValues(["first", "second"] as List<String>)
      parameter.addValues(["third"] as Set<String>)
      
    then: "we expect that the parameter keep the value registered in"
      parameter.valueCount == 3
      Arrays.equals(parameter.getValues(), ["first", "second", "third"] as String[]) == true
  }
  
  def "it is possible to register an iterator of values into the parameter" () {
    given: "a valid empty MutableAPIRequestParameter"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      
    when: "we add some iterable of values to the parameter"
      parameter.addValues((["first", "second"] as List<String>).iterator())
      parameter.addValues((["third"] as Set<String>).iterator())
      
    then: "we expect that the parameter keep the value registered in"
      parameter.valueCount == 3
      Arrays.equals(parameter.getValues(), ["first", "second", "third"] as String[]) == true
  }
  
  def "it is possible to get the value of the parameter at a given index" () {
    given: "a valid empty MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "third"] as String[])
    
    expect: "to get a valid value for each parameter index"
      parameter.getValue(0).equals("first") == true
      parameter.getValue(1).equals("second") == true
      parameter.getValue(2).equals("third") == true
  }
  
  def "it is possible to remove a value of the parameter at a given index" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we remove some values at a particular index"
      parameter.removeValue(2)
      parameter.removeValue(2)
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, ["first", "second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove a value of the parameter by its value" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we remove some values"
      parameter.removeValue("first")
      parameter.removeValue("third")
      parameter.removeValue("first")
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an array of values of the parameter" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we remove some values"
      parameter.removeValues(["first", "third"] as String[])
      parameter.removeValues(["first"] as String[])
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an iterable of values of the parameter" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we remove some values"
      parameter.removeValues(["first", "third"] as List<String>)
      parameter.removeValues(["first"] as Set<String>)
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an iterator of values of the parameter" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we remove some values"
      parameter.removeValues((["first", "third"] as List<String>).iterator())
      parameter.removeValues((["first"] as Set<String>).iterator())
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to clear the parameter of all of its values" () {
    given: "a valid MutableAPIRequestParameter with some values registered in"
      final MutableAPIRequestParameter parameter = new MutableAPIRequestParameter("parameter")
      parameter.addValues(["first", "second", "first", "third", "first", "second", "second"] as String[])
      
    when: "we clear the parameter of all of its values"
      parameter.clear()
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(parameter.values, [] as String[])
  }
}
