package hu.bcsabi.petstore.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Pet Store Order Service.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@SpringBootApplication
public class OrderServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
