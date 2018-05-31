package org.liara.api.request

import java.util.Map.Entry
import javax.servlet.http.HttpServletRequest
import spock.lang.Specification

import org.mockito.Mockito

public class MutableAPIRequestSpecification extends Specification
{
  def "it can be created from a map of parameters" () {
    given: "a map of parameters"
      final Map<String, String[]> parameters = [
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[] 
      ]
      
    when: "we create a MutableAPIRequest from the given map of parameters"
      final MutableAPIRequest request = MutableAPIRequest.from(parameters)
      
    then: "we expect to get a request with all parameters described in the given map"
      request.getParameterCount() == 3
    
      for (final Entry<String, String[]> entry : parameters.entrySet()) {
        Arrays.equals(entry.value, request.getValues(entry.key))
      }
  }
  
  def "it can be instantiated from a native HttpServletRequest" () {
    given: "an HttpServletRequest instance"
      final Map<String, String[]> parameters = [
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ]
      
      final HttpServletRequest httpRequest = Mockito.mock(HttpServletRequest.class)
      Mockito.when(httpRequest.getParameterMap()).thenReturn(parameters)
       
    when: "we instantiate a new MutableAPIRequest with the given HttpServletRequest"
      final MutableAPIRequest request = MutableAPIRequest.from(httpRequest)
      
    then: "we expect to have a well formed request with all the givens parameters"
      request.getParameterCount() == 3
      
      for (final Map.Entry<String, String[]> entry : parameters) {
        Arrays.equals(entry.value, request.getValues(entry.key))
      }
  }
  
  def "it can be instantiated without any parameters" () {
    given: "a default MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    expect: "the given request to not have any parameters"
      request.getParameterCount() == 0
  }
  
  def "it allow to check if the request contains a specific parameter" () {
    given: "a MutableAPIRequest with some parameters"
      final MutableAPIRequest request = MutableAPIRequest.from([
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[] 
      ])
      
    expect: "the request to be able to check if it contains some parameters"
      request.contains("first") == true
      request.contains("second") == true
      request.contains("third") == true
      request.contains("snaflogz") == false
  }
  
  def "it allow to register a parameter into the request" () {
    given: "a MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    and: "a MutableAPIRequestParameter instance"
      final MutableAPIRequestParameter parameter = Mockito.mock(MutableAPIRequestParameter.class)
      Mockito.when(parameter.getName()).thenReturn("first")
      
    when: "we add the parameter to the request"
      request.addParameter(parameter)
      
    then: "we expect the parameter to be added to the request"
      request.getParameterCount() == 1
      request.getParameter("first") == parameter
      
    and: "we expect the request to register itself into the parameter"
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
      
    when: "we add the parameter another time to the request"
      request.addParameter(parameter)
      
    then: "we expect the method to do nothing"
      request.getParameterCount() == 1
      request.getParameter("first") == parameter
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
  }
  
  def "it throw an error if you trying to add two parameters with the same name into the request" () {
    given: "a MutableAPIRequest with a first parameter"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["a", "b", "c"] as String[]    
      ])
      
    and: "another MutableAPIRequestParameter instance also named first"
      final MutableAPIRequestParameter parameter = Mockito.mock(MutableAPIRequestParameter.class)
      Mockito.when(parameter.getName()).thenReturn("first")
      
    when: "we add the parameter to the request"
      request.addParameter(parameter)
      
    then: "we expect the request to throw"
      thrown(Error.class)
  }
  
  def "it allow to register values to a parameter" () {
    given: "an empty MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    when: "we add values to a parameter"
      request.addValue("first", "first:first")
      request.addValue("first", "first:last")
      request.addValue("second", "second:first")
      
    then: "we expect the request to mutate accordingly"
      request.contains("first") == true
      request.contains("second") == true
      
      Arrays.equals(request.getValues("first"), ["first:first", "first:last"] as String[]) == true
      Arrays.equals(request.getValues("second"), ["second:first"] as String[]) == true
  }
  
  def "it allow to register multiple values as iterables to a parameter" () {
    given: "an empty MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    when: "we add values to a parameter"
      request.addValues("first", ["first:first", "first:second"] as Iterable<String>)
      request.addValues("first", ["first:last"] as Iterable<String>)
      request.addValues("second", ["second:first"] as Iterable<String>)
      
    then: "we expect the request to mutate accordingly"
      request.contains("first") == true
      request.contains("second") == true
      
      Arrays.equals(request.getValues("first"), ["first:first", "first:second", "first:last"] as String[]) == true
      Arrays.equals(request.getValues("second"), ["second:first"] as String[]) == true
  }
  
  def "it allow to register multiple values as arrays of values to a parameter" () {
    given: "an empty MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    when: "we add values to a parameter"
      request.addValues("first", ["first:first", "first:second"] as String[])
      request.addValues("first", ["first:last"] as String[])
      request.addValues("second", ["second:first"] as String[])
      
    then: "we expect the request to mutate accordingly"
      request.contains("first") == true
      request.contains("second") == true
      
      Arrays.equals(request.getValues("first"), ["first:first", "first:second", "first:last"] as String[]) == true
      Arrays.equals(request.getValues("second"), ["second:first"] as String[]) == true
  }
  
  def "it allow to register multiple values as iterators of values to a parameter" () {
    given: "an empty MutableAPIRequest"
      final MutableAPIRequest request = new MutableAPIRequest()
      
    when: "we add values to a parameter"
      request.addValues("first", ["first:first", "first:second"].iterator())
      request.addValues("first", ["first:last"].iterator())
      request.addValues("second", ["second:first"].iterator())
      
    then: "we expect the request to mutate accordingly"
      request.contains("first") == true
      request.contains("second") == true
      
      Arrays.equals(request.getValues("first"), ["first:first", "first:second", "first:last"] as String[]) == true
      Arrays.equals(request.getValues("second"), ["second:first"] as String[]) == true
  }
  
  def "it allow to unregister a parameter of the request by using its instance" () {      
    given: "a MutableAPIRequestParameter instance"
      final MutableAPIRequestParameter parameter = Mockito.mock(MutableAPIRequestParameter.class)
      Mockito.when(parameter.getName()).thenReturn("first")
      
    and: "a MutableAPIRequest with the given parameter registered in"
      final MutableAPIRequest request = new MutableAPIRequest()
      request.addParameter(parameter)
      
    when: "we remove the parameter from the request"
      request.removeParameter(parameter)
      
    then: "we expect the parameter to be added to the request"
      request.getParameterCount() == 0
      
    and: "we expect the request to register itself into the parameter"
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
      Mockito.verify(parameter, Mockito.times(1)).setRequest(null)
      
    when: "we remove the parameter another time from the request"
      request.removeParameter(parameter)
      
    then: "we expect the method to do nothing"
      request.getParameterCount() == 0
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
      Mockito.verify(parameter, Mockito.times(1)).setRequest(null)
  }
  
  def "it allow to unregister a parameter of the request by using its name" () {
    given: "a MutableAPIRequestParameter instance"
      final MutableAPIRequestParameter parameter = Mockito.mock(MutableAPIRequestParameter.class)
      Mockito.when(parameter.getName()).thenReturn("first")
      
    and: "a MutableAPIRequest with the given parameter registered in"
      final MutableAPIRequest request = new MutableAPIRequest()
      request.addParameter(parameter)
      
    when: "we remove the parameter from the request"
      request.removeParameter("first")
      
    then: "we expect the parameter to be added to the request"
      request.getParameterCount() == 0
      
    and: "we expect the request to register itself into the parameter"
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
      Mockito.verify(parameter, Mockito.times(1)).setRequest(null)
      
    when: "we remove the parameter another time from the request"
      request.removeParameter("first")
      
    then: "we expect the method to do nothing"
      request.getParameterCount() == 0
      Mockito.verify(parameter, Mockito.times(1)).setRequest(request)
      Mockito.verify(parameter, Mockito.times(1)).setRequest(null)
  }
  
  
  def "it is possible to remove a value of a parameter at a given index" () {
    given: "a MutableAPIRequest with some values registered in"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["first", "second", "first", "third", "first", "second", "second"] as String[]
      ])
      
    when: "we remove some values at a particular index"
      request.removeValue("first", 2)
      request.removeValue("first", 2)
      
    then: "we expect the request to be updated accordingly"
      Arrays.equals(request.getValues("first"), ["first", "second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove a value of a parameter by its value" () {
    given: "a MutableAPIRequest with some values registered in"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["first", "second", "first", "third", "first", "second", "second"] as String[]
      ])
      
    when: "we remove some values"
      request.removeValue("first", "first")
      request.removeValue("first", "third")
      request.removeValue("first", "first")
      
    then: "we expect the request to be updated accordingly"
      Arrays.equals(request.getValues("first"), ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an array of values of a parameter" () {
    given: "a MutableAPIRequest with some values registered in"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["first", "second", "first", "third", "first", "second", "second"] as String[]
      ])
      
    when: "we remove some values"
      request.removeValues("first", ["first", "third"] as String[])
      request.removeValues("first", ["first"] as String[])
      
    then: "we expect the request to be updated accordingly"
      Arrays.equals(request.getValues("first"), ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an iterable of values of a parameter" () {
    given: "a MutableAPIRequest with some values registered in"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["first", "second", "first", "third", "first", "second", "second"] as String[]
      ])
      
    when: "we remove some values"
      request.removeValues("first", ["first", "third"] as List<String>)
      request.removeValues("first", ["first"] as Set<String>)
      
    then: "we expect the request to be updated accordingly"
      Arrays.equals(request.getValues("first"), ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to remove an iterator of values of a parameter" () {
    given: "a MutableAPIRequest with some values registered in"
      final MutableAPIRequest request = new MutableAPIRequest([
        "first": ["first", "second", "first", "third", "first", "second", "second"] as String[]
      ])
      
    when: "we remove some values"
      request.removeValues("first", (["first", "third"] as List<String>).iterator())
      request.removeValues("first", (["first"] as Set<String>).iterator())
      
    then: "we expect that the parameter values to be updated accordingly"
      Arrays.equals(request.getValues("first"), ["second", "first", "second", "second"] as String[])
  }
  
  def "it is possible to get the number of values of a parameter" () {
    given: "a MutableAPIRequest with some parameters"
      final MutableAPIRequest request = MutableAPIRequest.from([
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ])
    
    expect: "to be able to get the number of values of each parameter"
      request.getValueCount("first") == 3
      request.getValueCount("second") == 2
      request.getValueCount("third") == 4
  }
  
  def "it is possible to get a value of a parameter" () {
    given: "a MutableAPIRequest with some parameters"
      final MutableAPIRequest request = MutableAPIRequest.from([
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ])
    
    expect: "to be able to get values of each parameter"
      request.getValue("first", 0).equals("first:first") == true
      request.getValue("first", 1).equals("first:second") == true
      request.getValue("first", 2).equals("first:last") == true
      request.getValue("second", 0).equals("second:first") == true
      request.getValue("second", 1).equals("second:last") == true
      request.getValue("third", 0).equals("third:first") == true
      request.getValue("third", 1).equals("third:second") == true
      request.getValue("third", 2).equals("third:third") == true
      request.getValue("third", 3).equals("third:last") == true
  }
  
  def "it is possible to get all parameters registered into the request" () {
    given: "a MutableAPIRequest with some parameters"
      final MutableAPIRequest request = MutableAPIRequest.from([
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ])
      
    expect: "to be able to get all parameters of the request"
      final Set<? extends MutableAPIRequestParameter> parameters = request.parameters
      
      parameters.size() == 3
      parameters.contains(request.getParameter("first")) == true
      parameters.contains(request.getParameter("second")) == true
      parameters.contains(request.getParameter("third")) == true
  }
  
  def "it allow to get an iterator over all parameters of the request" () {
    given: "a MutableAPIRequest with some parameters"
      final MutableAPIRequest request = MutableAPIRequest.from([
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ])
      
    expect: "to be able to get a valid iterator over all parameters of the request"
      final Iterator<? extends MutableAPIRequestParameter> iterator = request.iterator()
      final Set<? extends MutableAPIRequestParameter> parameters = new HashSet<>(request.parameters)
      int iterations = 0
      
      while (iterator.hasNext()) {
        parameters.remove(iterator.next())
        ++iterations
      }
      
      parameters.size() == 0
      iterations == 3
  }
  
  def "it allows to get a sub request by filtering request's parameters by a prefix" () {
    given: "a valid MutableAPIRequest"
      final Map<String, String[]> parameters = [
        "first": ["first:first", "first:second", "first:last"] as String[],
        "child.second": ["second:first", "second:last"] as String[],
        "child.third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ]
      
      final MutableAPIRequest request = new MutableAPIRequest(parameters)
  
    expect: "to get a valid sub-request by calling #subRequest"
      final MutableAPIRequest subRequest = request.subRequest("child")
      
      subRequest.parameterCount == 2
      
      Arrays.equals(subRequest.getValues("second"), request.getValues("child.second"))
      Arrays.equals(subRequest.getValues("third"), request.getValues("child.third"))
  }
}
