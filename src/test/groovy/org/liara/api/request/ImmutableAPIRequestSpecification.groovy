package org.liara.api.request

import java.security.Policy.Parameters

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.springframework.http.client.ClientHttpRequestFactory

import spock.lang.Specification

class ImmutableAPIRequestSpecification extends Specification
{  
  def "it can be instantiated as a new empty ImmutableAPIRequest" () {
    when: "we instantiate an new ImmutableAPIRequest without any parameters"
      final ImmutableAPIRequest request = new ImmutableAPIRequest();
      
    then: "we expect to have an empty api request instance"
      request.getParameterCount() == 0
  }
  
  def "it can be instantiated from a map of parameters values" () {
    given: "a map of parameters values"
      final Map<String, String[]> parameters = [
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ]
      
     when: "we instantiate a new ImmutableAPIRequest with the given map of parameters"
       final ImmutableAPIRequest request = new ImmutableAPIRequest(parameters)
       
     then: "we expect to have a well formed request with all the givens parameters"
       request.getParameterCount() == 3
       
       for (final Map.Entry<String, String[]> entry : parameters) {
         final ImmutableAPIRequestParameter parameter = request.getParameter(entry.key)
         
         request.contains(entry.key) == true
         parameter.name.equals(entry.key) == true
         parameter.request == request
         parameter.valueCount == entry.value.length
         
         for (int index = 0; index < entry.value.length; ++index) {
           parameter.getValue(index).equals(entry.value[index]) == true
         }
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
      Mockito.when(httpRequest.getParameterValues(Mockito.anyString())).thenAnswer({
        InvocationOnMock invocation ->
          def (String name) = invocation.getArguments()
          return parameters.get(name)
      })
       
    when: "we instantiate a new ImmutableAPIRequest with the given HttpServletRequest"
      final ImmutableAPIRequest request = new ImmutableAPIRequest(httpRequest)
      
    then: "we expect to have a well formed request with all the givens parameters"
      request.getParameterCount() == 3
      
      for (final Map.Entry<String, String[]> entry : parameters) {
        final ImmutableAPIRequestParameter parameter = request.getParameter(entry.key)
        
        request.contains(entry.key) == true
        parameter.name.equals(entry.key) == true
        parameter.request == request
        parameter.valueCount == entry.value.length
        
        for (int index = 0; index < entry.value.length; ++index) {
          parameter.getValue(index).equals(entry.value[index]) == true
        }
      }
  }
  
  def "it can be instantiated from another APIRequest instance" () {
    given: "an APIRequest instance"
      final Map<String, String[]> parameters = [
        "first": ["first:first", "first:second", "first:last"] as String[],
        "second": ["second:first", "second:last"] as String[],
        "third": ["third:first", "third:second", "third:third", "third:last"] as String[]
      ]
      
      final APIRequest baseRequest = new ImmutableAPIRequest(parameters)
       
    when: "we instantiate a new ImmutableAPIRequest with the given APIRequest"
      final ImmutableAPIRequest copiedRequest = new ImmutableAPIRequest(baseRequest)
      
    then: "we expect to have a valid copy of the given APIRequest"
      copiedRequest.getParameterCount() == 3
      
      for (final APIRequestParameter baseParameter : baseRequest) {
        copiedRequest.contains(baseParameter.name) == true
        
        final ImmutableAPIRequestParameter copiedParameter = copiedRequest.getParameter(
          baseParameter.name
        )
        
        copiedParameter.name.equals(baseParameter.name) == true
        copiedParameter.request == copiedRequest
        copiedParameter.valueCount == baseParameter.valueCount
        
        for (int index = 0; index < baseParameter.valueCount; ++index) {
          copiedParameter.getValue(index).equals(baseParameter.getValue(index)) == true
        }
      }
  }
}
