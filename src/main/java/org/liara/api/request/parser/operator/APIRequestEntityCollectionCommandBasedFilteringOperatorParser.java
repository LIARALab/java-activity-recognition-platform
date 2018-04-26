/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.request.parser.operator;

import java.util.Arrays;
import java.util.List;

import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.filtering.EntityCollectionCommandBasedFilteringOperator;
import org.liara.api.filter.interpretor.FilterInterpretor;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIDocumentedRequestParser;
import org.springframework.lang.NonNull;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;

public class      APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Field> 
       implements APIRequestEntityCollectionOperatorParser<Entity>, APIDocumentedRequestParser
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final FilterInterpretor<Entity, Field> _interpretor;
  
  public APIRequestEntityCollectionCommandBasedFilteringOperatorParser (
    @NonNull final String parameter, 
    @NonNull final FilterInterpretor<Entity, Field> interpretor
  ) {
    _parameter = parameter;
    _interpretor = interpretor;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      return new EntityCollectionCommandBasedFilteringOperator<>(
          request.getParameter(_parameter).getValues(), 
          _interpretor
      );
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }

  @Override
  public List<Parameter> getHandledParametersDocumentation (@NonNull final List<APIDocumentedRequestParser> parents) {
    String fieldName = this.getFullName(parents);
    
    if (fieldName.length() > 0) {
      fieldName += "." + _parameter;
    } else {
      fieldName = _parameter;
    }
    
    return Arrays.asList(
      new ParameterBuilder()
      .name(fieldName)
      .allowMultiple(true)
      .required(false)
      .type(new TypeResolver().resolve(String.class))
      .modelRef(new ModelRef("string"))
      .parameterType("query")
      .pattern(_interpretor.getValidator().getBestMatchPattern())
      .description(String.join("", 
        "Filter the field ", fieldName, " by using a filtering command.",
        " Refer to the documentation of the class ", 
        _interpretor.getValidator().getClass().toString(),
        " for more information about the command structure."
       )).build()
    );
  }
}
