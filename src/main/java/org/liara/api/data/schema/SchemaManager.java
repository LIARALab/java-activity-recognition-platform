package org.liara.api.data.schema;

import org.springframework.lang.NonNull;

public interface SchemaManager
{
  public void begin ();
  
  public void finish ();
  
  public void rollback ();
  
  public <Entity> Entity execute (@NonNull final Object schema);

  public void flush ();
  
  public void clear ();
}
