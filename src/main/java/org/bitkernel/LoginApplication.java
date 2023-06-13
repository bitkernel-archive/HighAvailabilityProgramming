package org.bitkernel;

import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.stats.BikCacheStats;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@EnableCaching
@Slf4j
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@ComponentScan({"org.bitkernel.*", "io.bikcache.*"})
public class LoginApplication {
    public static ConfigurableApplicationContext ac;

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        ac = SpringApplication.run(LoginApplication.class, args);
        logger.info("Hello World.");
        ScheduledExecutorService telemetry = Executors.newSingleThreadScheduledExecutor();
        telemetry.scheduleAtFixedRate(() -> {
            BikCacheStats stats = CacheChannel.stats();
            logger.info(stats.toString());
        }, 0, 1, TimeUnit.SECONDS);
    }
}
