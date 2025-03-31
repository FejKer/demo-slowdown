package pl.omnilogy.demoslowdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoSlowdownApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSlowdownApplication.class, args);
    }

}
