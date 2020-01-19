package com.usrun.core.config.cache;

import com.usrun.core.config.AppProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Autowired
    private AppProperties appProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setTimeout(10000000)
                .setAddress(appProperties.getRedisUrl())
                .setConnectionPoolSize(10).setConnectionMinimumIdleSize(10);
//        config.setCodec(StringCodec.INSTANCE);
        KryoCodec kryoCodec = new KryoCodecWithDefaultSerializer();
        config.setCodec(kryoCodec);
        return Redisson.create(config);
    }
}
