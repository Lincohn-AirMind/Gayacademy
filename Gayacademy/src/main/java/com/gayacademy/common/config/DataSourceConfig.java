package com.gayacademy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.gayacademy.user.repository",
        "com.gayacademy.content.repository",
        "com.gayacademy.community.repository",
        "com.gayacademy.feed.repository"
})
@EnableMongoRepositories(basePackages = "com.gayacademy.chat.repository")
@EnableRedisRepositories(basePackages = "com.gayacademy.feed.cache")
public class DataSourceConfig {
}