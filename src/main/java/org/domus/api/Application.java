package org.domus.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import org.springframework.context.annotation.Import;

import org.domus.api.configuration.SwaggerConfiguration;

@SpringBootApplication
@Import({
  SwaggerConfiguration.class
})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
