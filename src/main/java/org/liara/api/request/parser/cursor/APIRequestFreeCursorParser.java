/**
 * Copyright 2018 Cédric DEMONGIVERT
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

import org.liara.api.collection.cursor.Cursor;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIRequestParser;
import org.springframework.lang.NonNull;

/**
 * A parser that takes a "first" or "all" parameter and an "after" parameter and
 * return a cursor from them.
 * 
 * It allow the client to get any slice of data from a collection without any
 * restriction.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class APIRequestFreeCursorParser implements APIRequestParser<Cursor>
{
  /**
   * @see org.domus.api.request.parser.APIRequestParser#parse(org.domus.api.request.APIRequest);
   */
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

}
