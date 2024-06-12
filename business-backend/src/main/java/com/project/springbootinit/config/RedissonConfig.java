package com.project.springbootinit.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")       //  动态读取配置 读取yml中的Redis配置
@Data
@Slf4j
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

    private String password;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database) //  这里使用 2库来实现限流
                .setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }


}
