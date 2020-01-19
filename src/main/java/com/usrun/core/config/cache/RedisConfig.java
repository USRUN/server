package com.usrun.core.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {

    private String url;
    private String password;

    @Bean
    @Profile("dev")
    public RedissonClient redissonClientDev() {
        Config config = new Config();
        config.useSingleServer()
                .setTimeout(10000000)
                .setAddress(url)
                .setConnectionPoolSize(10).setConnectionMinimumIdleSize(10);
//        config.setCodec(StringCodec.INSTANCE);
        KryoCodec kryoCodec = new KryoCodecWithDefaultSerializer();
        config.setCodec(kryoCodec);
        return Redisson.create(config);
    }

    @Bean
    @Profile("!dev")
    public RedissonClient redissonClientPro() {
        Config config = new Config();
        config.useSingleServer()
                .setTimeout(10000000)
                .setAddress(url)
                .setPassword(password)
                .setConnectionPoolSize(10).setConnectionMinimumIdleSize(10);
//        config.setCodec(StringCodec.INSTANCE);
        KryoCodec kryoCodec = new KryoCodecWithDefaultSerializer();
        config.setCodec(kryoCodec);
        return Redisson.create(config);
    }
}
