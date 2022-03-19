package task4.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static ConfigurableApplicationContext main(String[] args) {
        return SpringApplication.run(Application.class, args);
    }
}