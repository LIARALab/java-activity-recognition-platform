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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.recognition.sensor.VirtualSensorManager;
import org.liara.data.graph.Column;
import org.liara.data.graph.Graph;
import org.liara.data.graph.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import java.util.TimeZone;
import java.util.logging.Logger;

@SpringBootApplication
@ComponentScan({"org.liara.api", "org.liara.rest"})
@Import({})
public class Application
{
  public static void main (@NonNull final String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    @NonNull final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    logDataGraph(context);

    context.getBean(VirtualSensorManager.class).start();
  }

  private static void logDataGraph (@NonNull final ConfigurableApplicationContext context) {
    @NonNull final Logger logger = Logger.getLogger("data-graph");
    @NonNull final Graph  graph  = context.getBean("dataGraph", Graph.class);

    logger.info("Application started with data-graph \"" + graph.getName() + "\"");

    for (int index = 0, size = graph.getTables().getSize(); index < size; ++index) {
      @NonNull final Table table = graph.getTables().get(index);

      logger.info("  with table \"" + table.getName() + "\"");
      logDataGraphTableColumns(table);
    }
  }

  private static void logDataGraphTableColumns (@NonNull final Table table) {
    @NonNull final Logger logger = Logger.getLogger("data-graph");

    for (int index = 0, size = table.getColumns().getSize(); index < size; ++index) {
      @NonNull final Column column = table.getColumns().get(index);

      logger.info(
        "    with column \"" + column.getName() + "\" of type " + column.getType().toString()
      );
    }
  }

  @Bean
  public @NonNull Metamodel getMetamodel (
    @NonNull final EntityManagerFactory entityManagerFactory
  ) {
    return entityManagerFactory.getMetamodel();
  }
}
