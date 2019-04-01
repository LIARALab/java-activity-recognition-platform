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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import java.util.TimeZone;

@SpringBootApplication
@ComponentScan({"org.liara.api", "org.liara.rest"})
@Import({})
public class Application
{
  public static void main (@NonNull final String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    @NonNull final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    context.getBean(VirtualSensorManager.class).start();
  }

  @Bean
  public @NonNull Metamodel getMetamodel (
    @NonNull final EntityManagerFactory entityManagerFactory
  ) {
    return entityManagerFactory.getMetamodel();
  }
}
