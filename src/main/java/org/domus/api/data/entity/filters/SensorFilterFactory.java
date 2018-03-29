/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.domus.api.data.entity.filters;

import org.domus.api.data.entity.Sensor;
import org.domus.api.request.parser.DateTimeFilterParser;
import org.domus.api.request.parser.EntityFilterParser;
import org.domus.api.request.parser.FilterParser;
import org.domus.api.request.parser.IntegerFilterParser;
import org.domus.api.request.validator.DateTimeFilterValidator;
import org.domus.api.request.validator.EntityFilterValidator;
import org.domus.api.request.validator.FilterValidator;
import org.domus.api.request.validator.IntegerFilterValidator;

public final class SensorFilterFactory implements EntityFilterFactory<Sensor>
{
  public FilterParser<Sensor> createParser () {
    final EntityFilterParser<Sensor> parser = new EntityFilterParser<>();
    
    parser.getParsers().put("_identifier", new IntegerFilterParser("identifier"));
    parser.getParsers().put("_creationDate", new DateTimeFilterParser("creationDate"));
    
    return parser;
  }
  
  public FilterValidator createValidator () {
    return new EntityFilterValidator(
      new IntegerFilterValidator("identifier"),
      new DateTimeFilterValidator("creationDate")
    );
  }
}
