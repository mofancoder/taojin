package com.tj.util.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Configuration
@ConditionalOnClass({SacEmail.class, JavaMailSender.class})
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = "spring.mail", name = "host", matchIfMissing = false)
public class EmailAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:template");
        return configurer;
    }


    @Bean
    public SacEmail sacEmail(JavaMailSender javaMailSender, MailProperties mailProperties, FreeMarkerConfigurer freeMarkerConfigurer) {
        return new SacEmail(javaMailSender, mailProperties, freeMarkerConfigurer);
    }

}
