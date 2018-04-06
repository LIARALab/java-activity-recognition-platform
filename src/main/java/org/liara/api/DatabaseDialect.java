package org.liara.api;

import org.hibernate.dialect.MySQL5Dialect;

public class DatabaseDialect extends MySQL5Dialect {
  public DatabaseDialect() {
      super();
      registerKeyword("microsecond");
  }
}
