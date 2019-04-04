package com.tj.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-20 19:41
 **/
@SpringBootApplication(scanBasePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.transaction.dao"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableAsync
public class TjTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TjTransactionApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
