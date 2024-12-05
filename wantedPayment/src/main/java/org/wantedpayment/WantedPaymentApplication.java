package org.wantedpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WantedPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(WantedPaymentApplication.class, args);
    }

}
