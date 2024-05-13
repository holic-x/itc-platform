package com.noob.module.admin.common.email.service;

/**
 * 邮件服务接口定义
 */
public interface EmailService {

    /**
     * 发送邮件
     * @param to
     * @param subject
     * @param content
     */
    void sendMail(String to, String subject, String content);

}
