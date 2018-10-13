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
package org.liara.api.request.cursor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.cursoring.Cursor;
import org.liara.request.APIRequest;
import org.liara.request.parser.APIRequestParser;

/**
 * A parser that check for the presence of a "first", "all" and "after" parameters and and return a cursor from them.
 *
 * It allow the client to get any slice of data from a collection without any restriction.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
public class APIRequestFreeCursorParser
  implements APIRequestParser<Operator>
{
  @Override
  public @NonNull Cursor parse (@NonNull final APIRequest request) {
    final int offset = request.getParameter("offset").getAsInteger(0).orElse(0);

    if (request.contains("first")) {
      return Cursor.NONE.setOffset(offset).setLimit(request.getParameter("first").getAsInteger(0).orElse(10));
    } else if (request.getParameter("all").getAsBoolean(0).orElse(false)) {
      return Cursor.ALL.setOffset(offset);
    } else {
      return Cursor.NONE.setLimit(10).setOffset(offset);
    }
  }

  /*
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
  }*/
}
