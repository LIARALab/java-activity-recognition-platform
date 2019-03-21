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

import org.liara.api.configuration.SwaggerConfiguration;
import org.liara.api.recognition.sensor.VirtualSensorManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.logging.Logger;

@SpringBootApplication
@Import({
  SwaggerConfiguration.class
})
public class Application
{
  private static String[] ARGUMENTS = new String[0];

  public static String[] getStartingArguments () {
    return Arrays.copyOf(Application.ARGUMENTS, Application.ARGUMENTS.length);
  }

  public static boolean isFlagPassed (final String fullName) {
    final String argumentToFind = "--" + fullName;

    for (final String argument : Application.ARGUMENTS) {
      if (argument.trim().equalsIgnoreCase(argumentToFind)) return true;
    }

    return false;
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer getPropertyPlaceholderConfigurer () {
    final PropertySourcesPlaceholderConfigurer result = new PropertySourcesPlaceholderConfigurer();

    if (isFlagPassed("development")) {
      Logger.getLogger(Application.class.toString()).info(
        "Application launched in development mode, see application-development.properties file " +
        "for more information.");
      result.setLocation(new ClassPathResource("application-development.properties"));
    } else {
      result.setLocation(new ClassPathResource("application.properties"));
    }

    return result;
  }

  public static void main (String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Application.ARGUMENTS = args;

    final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    context.getBean(VirtualSensorManager.class).start();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer()
    {
      @Override
      public void addCorsMappings (CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOrigins("*")
          .allowedHeaders("*")
          .allowCredentials(true)
          .allowedMethods("*");
      }
    };
  }
}
