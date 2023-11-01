package com.lukaszpopek.JobOffersBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JobOffersBoardApplication {

  public static void main(String[] args) {
    SpringApplication.run(JobOffersBoardApplication.class, args);
  }

}
