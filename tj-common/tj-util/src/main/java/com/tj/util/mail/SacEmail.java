package com.tj.util.mail;

import freemarker.template.Template;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SacEmail {
    private JavaMailSender javaMailSender;
    private FreeMarkerConfigurer freeMarkerConfigurer;
    private MailProperties mailProperties;

    SacEmail(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    SacEmail(JavaMailSender javaMailSender, MailProperties mailProperties, FreeMarkerConfigurer freeMarkerConfigurer) {
        this.javaMailSender = javaMailSender;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.mailProperties = mailProperties;
    }

    /**
     * 发送简单邮件
     *
     * @param to      邮件目的地
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendExceptionMail(String to, String subject, Throwable e) {
        List<String> stacks = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(toList());
        stacks.add(0, e.toString());
        String content = String.join("\n", stacks);
        sendSimpleMail(to, subject, content);
    }

    /**
     * 发送html 邮件
     *
     * @param to      邮件目的
     * @param subject 主题
     * @param html    文档
     */
    public void sendHtmlMail(String to, String subject, String html) {
        MimeMessage mimeMessage = null;
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(html, true);
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送附件邮件
     *
     * @param to      邮件目的地
     * @param subject 主题
     * @param file    文件
     */
    public void sendAttachmentMail(String to, String subject, String text, File file) {
        MimeMessage mimeMessage = null;
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            FileSystemResource resource = new FileSystemResource(file);
            messageHelper.addAttachment(file.getName(), resource);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送模板邮件
     *
     * @param to              邮件目的地
     * @param subject         主题
     * @param placeholders    参数占位符
     * @param templateAddress 模板地址
     */
    public void sendTemplateMail(String to, String subject, Map<String, Object> placeholders, String templateAddress) {
        MimeMessage mimeMessage = null;
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateAddress);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, placeholders);
            messageHelper.setText(text, true);

            javaMailSender.send(messageHelper.getMimeMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
