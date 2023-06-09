package org.bitkernel.config;

import io.bikcache.core.BikCacheApi;
import io.bikcache.core.BikCacheConfig;
import net.oschina.j2cache.CacheChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BikCacheConfiguration {
    @Bean
    public CacheChannel initBikCache() throws IOException {
        BikCacheConfig.RedisConfig lettuceConfig = BikCacheConfig.RedisConfig.builder()
                .mode("single")
                .hosts("127.0.0.1:6379")
                .password("")
                .build();

        BikCacheConfig cacheConfig = BikCacheConfig.builder()
                .l2KeyNamespace("bitkernel.high_availability.programming.loginApplication")
                .build();

        return BikCacheApi.init(cacheConfig, lettuceConfig);
    }
}
