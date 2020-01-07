package co.fun.code.funcorpchallengeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.openapitools", "co.fun.code.funcorpchallengeservice.api" , "org.openapitools.configuration"})
public class FuncorpChallengeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FuncorpChallengeServiceApplication.class, args);
  }

}
