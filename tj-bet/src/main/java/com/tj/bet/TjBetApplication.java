package com.tj.bet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-20 19:41
 **/
@SpringBootApplication
@ComponentScan(basePackages = {"com.tj.*"})
@MapperScan(basePackages = {"com.tj.bet.dao"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableAsync
public class TjBetApplication {
    //统一设置时区


    public static void main(String[] args) {
        SpringApplication.run(TjBetApplication.class, args);
    }

}
