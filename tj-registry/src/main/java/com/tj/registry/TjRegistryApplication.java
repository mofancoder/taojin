package com.tj.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-20 19:41
 **/
@SpringBootApplication
@EnableEurekaServer
public class TjRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TjRegistryApplication.class, args);
    }

}
