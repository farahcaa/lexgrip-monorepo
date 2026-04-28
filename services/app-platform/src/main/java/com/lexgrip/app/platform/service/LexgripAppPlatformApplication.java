package com.lexgrip.app.platform.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@ComponentScan(basePackages = {"com.lexgrip"})
@EnableScheduling
public class LexgripAppPlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(LexgripAppPlatformApplication.class, args);
  }
}
