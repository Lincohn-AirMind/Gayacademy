package com.gayacademy.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    private String secret;
    private String issuer = "gayacademy-api";
    private long accessTokenExpirationMinutes = 15;
    private long refreshTokenExpirationDays = 30;

    public Duration getAccessTokenExpiration() {
        return Duration.ofMinutes(accessTokenExpirationMinutes);
    }

    public Duration getRefreshTokenExpiration() {
        return Duration.ofDays(refreshTokenExpirationDays);
    }
}