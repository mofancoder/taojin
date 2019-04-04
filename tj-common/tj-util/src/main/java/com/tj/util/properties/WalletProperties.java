package com.tj.util.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wallet")
@Data
public class WalletProperties {
    private Cross cross;
    private Token token;
    private Application application;

    @Data
    public static class Cross {
        private String domain;
    }

    @Data
    public static class Token {
        private Long outtime;
    }

    @Data
    public static class Application {
        private boolean test;
    }
}
