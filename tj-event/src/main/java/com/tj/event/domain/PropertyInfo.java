package com.tj.event.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PropertyInfo {
    @Value("${crawl.env.webdriver}")
    private String WEBDRIVER;
    @Value("${crawl.env.chrome}")
    private String CHROME;

    @Value("${crawl.url.score}")
    private String SITEURL;
    @Value("${crawl.url.live}")
    private String LIVEURL;
    @Value("${crawl.url.pre_hist}")
    private String INDEXHIST;
    @Value("${crawl.url.trend}")
    private String TRADEINFO;

}
