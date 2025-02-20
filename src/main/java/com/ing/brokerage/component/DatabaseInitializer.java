package com.ing.brokerage.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DatabaseInitializer implements CommandLineRunner {

  private final JdbcTemplate jdbcTemplate;

  public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    String sql = new String(Files.readAllBytes(Paths.get(new ClassPathResource("init.sql").getURI())));
    jdbcTemplate.execute(sql);
  }
}
