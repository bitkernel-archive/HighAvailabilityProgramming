package org.bitkernel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@ComponentScan({"org.bitkernel.*", "io.bikcache.*"})
@Slf4j
public class LoginApplication {
    public static ConfigurableApplicationContext ac;

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        ac = SpringApplication.run(LoginApplication.class, args);
        logger.info("Hello World.");
    }
}
