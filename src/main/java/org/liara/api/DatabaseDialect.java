package org.liara.api;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;

public class DatabaseDialect extends MySQL5Dialect {
  public DatabaseDialect() {
      super();
      registerKeyword("microsecond");
      registerFunction("regexp", new SQLFunctionTemplate(IntegerType.INSTANCE, "?1 REGEXP ?2"));
  }
}
