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
package org.liara.api;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

public class DatabaseDialect
  extends MySQL8Dialect
{
  public DatabaseDialect() {
      super();
      registerKeyword("microsecond");
      registerFunction(
        "timestampdiff_microsecond", 
        new SQLFunctionTemplate(
          LongType.INSTANCE, 
          "TIMESTAMPDIFF(MICROSECOND, ?1, ?2)"
        )
      );
    registerFunction(
      "date_sub_hour",
      new SQLFunctionTemplate(StringType.INSTANCE, "DATE_SUB(?1, INTERVAL ?2 HOUR)")
    );
    registerFunction(
      "date_add_hour",
      new SQLFunctionTemplate(StringType.INSTANCE, "DATE_ADD(?1, INTERVAL ?2 HOUR)")
    );
    registerFunction(
        "regexp",
        new SQLFunctionTemplate(
          BooleanType.INSTANCE,
          "?1 REGEXP ?2"
        )
      );
  }
}
