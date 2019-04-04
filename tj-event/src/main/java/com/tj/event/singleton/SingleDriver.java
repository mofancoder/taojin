package com.tj.event.singleton;

import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

@Data
public class SingleDriver {

    private volatile static WebDriver driver;

    private SingleDriver (){}

    @Value("${crawl.env.webdriver}")
    private static String WEBDRIVER;
    @Value("${crawl.env.chrome}")
    private static String CHROME;

    public static WebDriver getInstance() {
        if (driver != null) {
            return driver;
        }
        synchronized (SingleDriver.class) {
            if (driver != null) {
                return driver;
            }
            System.setProperty("webdriver.chrome.driver", WEBDRIVER);//chromedriver服务地址
            ChromeOptions options = new ChromeOptions();
            options.setBinary(CHROME);
            options.addArguments("--headless");//无界面参数
            options.addArguments("no-sandbox");//禁用沙盒

            driver = new ChromeDriver(options);
        }
        return driver;
    }
}
