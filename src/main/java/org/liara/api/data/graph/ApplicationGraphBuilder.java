package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.graph.Graph;
import org.liara.data.graph.builder.StaticGraphBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApplicationGraphBuilder
  implements GraphSupplier
{
  @NonNull
  private final ApplicationContext _context;

  @Autowired
  public ApplicationGraphBuilder (@NonNull final ApplicationContext context) {
    _context = context;
  }

  @Override
  public @NonNull String getName () {
    return "api";
  }

  @Bean("dataGraph")
  @Override
  public @NonNull Graph get () {
    @NonNull final StaticGraphBuilder builder = new StaticGraphBuilder();
    builder.setName(getName());

    @NonNull final Iterator<@NonNull TableSupplier> tables = (
      _context.getBeansOfType(TableSupplier.class).values().iterator()
    );

    while (tables.hasNext()) {
      @NonNull final TableSupplier table = tables.next();

      if (builder.containsTable(table.getName())) {
        throw new Error(
          "Unable to add the table " + table.getName() + " " + table.toString() + " to the " +
          "application data graph because the given table name was already used by another " +
          "table."
        );
      }

      builder.putTable(table.getName(), table.get());
    }

    return builder.build();
  }
}
