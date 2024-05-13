package com.noob.module.admin.common.email.service.impl;

import com.noob.module.admin.common.email.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮箱服务实现
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.email}")
    private String email;

    @Override
    public void sendMail(String to, String subject, String content) {
        // 读取邮箱配置
        if (email == null) {
            throw new RuntimeException("邮箱配置异常");
        }

        // 创建邮件消息
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            // 设置发件人邮箱
            helper.setFrom(email);
            // 设置收件人信息
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        // 发送邮件
        mailSender.send(message);
    }
}
