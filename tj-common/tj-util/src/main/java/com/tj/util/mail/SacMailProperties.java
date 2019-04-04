package com.tj.util.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sac.mail")
@Data
public class SacMailProperties {
    private String host;
    private String username;
    private String password;
}
