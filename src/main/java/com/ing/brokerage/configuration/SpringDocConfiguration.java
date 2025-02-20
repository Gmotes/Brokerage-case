package com.ing.brokerage.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SpringDocConfiguration {

  @Value("${app.custom.env}")
  private String env;

  /**
   * @return a new OpenAPI instance.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .servers(List.of(
            new Server().url("https://api.brockage.ing.com/" + env + "/").description("Server")
        ));
  }
}