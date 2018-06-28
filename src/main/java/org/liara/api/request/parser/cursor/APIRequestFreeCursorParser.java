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
/**
 * Copyright 2018 Cedric DEMONGIVERT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.liara.api.request.parser.cursor;

import java.util.Arrays;
import java.util.List;

import org.liara.api.collection.transformation.cursor.Cursor;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIDocumentedRequestParser;
import org.liara.api.request.parser.APIRequestParser;
import org.springframework.lang.NonNull;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;

/**
 * A parser that takes a "first" or "all" parameter and an "after" parameter and
 * return a cursor from them.
 * 
 * It allow the client to get any slice of data from a collection without any
 * restriction.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
public class APIRequestFreeCursorParser implements APIRequestParser<Cursor>, APIDocumentedRequestParser
{
  @Override
  public Cursor parse (@NonNull final APIRequest request) {
    int offset = 0;
    int limit = 10;

    if (request.contains("after")) {
      offset = Integer.parseInt(request.getValue("after", 0).trim());
    }

    if (request.contains("first")) {
      limit = Integer.parseInt(request.getValue("first", 0).trim());
    } else if (request.contains("all")) {
      final String value = request.getValue("all", 0).trim();

      if (value.equals("") || value.equals("true")) {
        return Cursor.ALL.setOffset(offset);
      }
    }

    return new Cursor(offset, limit);
  }

  @Override
  public List<Parameter> getHandledParametersDocumentation (@NonNull final List<APIDocumentedRequestParser> parents) {
    final TypeResolver resolver = new TypeResolver();
    
    return Arrays.asList(
      new ParameterBuilder()
        .name("first")
        .allowMultiple(false)
        .required(false)
        .defaultValue("10")
        .type(resolver.resolve(Long.class))
        .parameterType("query")
        .pattern("^\\d+$")
        .modelRef(new ModelRef("long"))
        .description(String.join("", 
          "Maximum number of elements to display. Must be a positive ",
          "integer and can't be used in conjunction with \"all\"."
         ))
        .build(),
      new ParameterBuilder()
        .name("all")
        .allowMultiple(false)
        .required(false)
        .defaultValue("false")
        .type(resolver.resolve(Boolean.class))
        .modelRef(new ModelRef("boolean"))
        .parameterType("query")
        .pattern("^(true|false|0|1)$")
        .description(String.join("", 
          "Display all remaining elements. Can't be used in conjunction",
          "with \"first\"."
         ))
        .build(),
      new ParameterBuilder()
        .name("after")
        .allowMultiple(false)
        .required(false)
        .defaultValue("0")
        .type(resolver.resolve(Long.class))
        .modelRef(new ModelRef("long"))
        .parameterType("query")
        .pattern("^\\d+$")
        .description(String.join("", 
          "Number of elements to skip."
         ))
        .build()
    );
  }
}
