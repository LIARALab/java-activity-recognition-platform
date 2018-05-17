package org.liara.api.request

import org.mockito.Mockito;

import spock.lang.Specification

class ImmutableAPIRequestParameterSpecificiation extends Specification
{
  def "it can be instanciated for a given request, with a name and a value" () {
    given: "a request"
      final APIRequest request = Mockito.mock(APIRequest.class)
     
    and: "a parameter name"
      final String name = "parameter"
      
    and: "a value"
      final String value = "value"
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given data"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(request, name, value)
    
    then: "the new instance must have a valid request, name and values"
      parameter.getRequest() == request
      parameter.getName().equals(name) == true
      parameter.getValueCount() == 1
      parameter.getValue(0).equals(value) == true
  }
  
  def "it can be instanciated for a given request, with a name and an array of values" () {
    given: "a request"
      final APIRequest request = Mockito.mock(APIRequest.class)
     
    and: "a parameter name"
      final String name = "parameter"
      
    and: "an array of values"
      final String[] values = ["first", "second", "third", "last"]
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given data"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(request, name, values)
    
    then: "the new instance must have a valid request, name and values"
      parameter.getRequest() == request
      parameter.getName().equals(name) == true
      parameter.getValueCount() == values.length
      
      for (int index = 0; index < values.length; ++index) {
        parameter.getValue(index).equals(values[index]) == true
      }
  }
  
  def "it can be instanciated for a given request, with a name and a collection of values" () {
    given: "a request"
      final APIRequest request = Mockito.mock(APIRequest.class)
     
    and: "a parameter name"
      final String name = "parameter"
      
    and: "a collection of values"
      final Collection<String> values = ["first", "second", "third", "last"]
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given data"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(request, name, values)
    
    then: "the new instance must have a valid request, name and values"
      parameter.getRequest() == request
      parameter.getName().equals(name) == true
      parameter.getValueCount() == values.size()
      
      for (int index = 0; index < values.size(); ++index) {
        parameter.getValue(index).equals(values.get(index)) == true
      }
  }
  
  def "it can be instanciated for a given request, with a name and an iterable of parameter's values" () {
    given: "a request"
      final APIRequest request = Mockito.mock(APIRequest.class)
     
    and: "a parameter name"
      final String name = "parameter"
      
    and: "an iterable of values"
      final List<String> values = ["first", "second", "third", "last"]
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given data"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(
        request, name, values as Iterable<String>
      )
    
    then: "the new instance must have a valid request, name and values"
      parameter.getRequest() == request
      parameter.getName().equals(name) == true
      parameter.getValueCount() == values.size()
      
      for (int index = 0; index < values.size(); ++index) {
        parameter.getValue(index).equals(values.get(index)) == true
      }
  }
  
  def "it can be instanciated for a given request, with a name and an iterator over the parameter's values" () {
    given: "a request"
      final APIRequest request = Mockito.mock(APIRequest.class)
     
    and: "a parameter name"
      final String name = "parameter"
      
    and: "an iterator of values"
      final List<String> values = ["first", "second", "third", "last"]
      final Iterator<String> iterator = values.iterator() as Iterator<String>
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given data"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(
        request, name, iterator
      )
    
    then: "the new instance must have a valid request, name and values"
      parameter.getRequest() == request
      parameter.getName().equals(name) == true
      parameter.getValueCount() == values.size()
      
      for (int index = 0; index < values.size(); ++index) {
        parameter.getValue(index).equals(values.get(index)) == true
      }
  }
  
  def "it can be instanciated from another parameter instance" () {
    given: "two requests"
      final APIRequest baseRequest = Mockito.mock(APIRequest.class)
      final APIRequest copyRequest = Mockito.mock(APIRequest.class)
     
    and: "a parameter instance"
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(
        baseRequest, "base", ["first", "second", "third", "last"] as String[]
      )
      
    when: "we create a new instance of ImmutableAPIRequestParameter with the given parameter"
      final ImmutableAPIRequestParameter copy = new ImmutableAPIRequestParameter(
        copyRequest, parameter
      )
    
    then: "the new instance must have a valid request, name and values"
      copy.getRequest() == copyRequest
      copy.getName().equals(parameter.getName()) == true
      copy.getValueCount() == parameter.getValueCount()
      
      for (int index = 0; index < parameter.getValueCount(); ++index) {
        copy.getValue(index).equals(parameter.getValue(index)) == true
      }
  }
  
  def "it allows the user to get a copy of the parameter values by using #getValues()" () {
    given: "a valid parameter instance"
      final String[] values = ["first", "second", "third", "last"]
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(
        Mockito.mock(APIRequest.class), "parameter",  values
      )
      
    when: "we call #getValues()"
      final String[] valueCopy = parameter.getValues()
       
    then: "we get a copy of the inner parameters"
      valueCopy.length == parameter.getValueCount()
      
      for (int index = 0; index < valueCopy.length; ++index) {
        parameter.getValue(index).equals(valueCopy[index]) == true
      }
       
    when: "we mutate the returned copy"
      valueCopy[2] = "wagabada"
       
    then: "the internal parameter state does not change"
      parameter.getValue(2).equals("third") == true
  }
  
  def "it allow the user to iterate over the parameter's values by using the instance" () {
    given: "a valid parameter instance"
      final String[] values = ["first", "second", "third", "last"]
      final ImmutableAPIRequestParameter parameter = new ImmutableAPIRequestParameter(
        Mockito.mock(APIRequest.class), "parameter",  values
      )
      
    when: "when we iterate over the parameter"
      final List<String> content = []
      for (final String value : parameter) content.add(value)
        
    then: "we expect to get all values of the parameter in their declaration order"
      content.size() == values.length
      
      for (int index = 0; index < values.length; ++index) {
        content.get(index).equals(values[index]) == true
      }
  }
}
