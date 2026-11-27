package com.example.clothes_website.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    private String name = "refresh_token";
    private boolean httpOnly = true;
    private boolean secure = false;
    private String sameSite = "Lax";
    private String domain;
    private String path = "/api/v1/auth";
}
