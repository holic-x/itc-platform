package com.noob.module.account.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.BusinessException;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.common.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:19
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "noob";

    @Resource
    private EmailService emailService;

//    @Resource
//    private RedisTemplate<String,Object> redisTemplate;

    @Value("${custom.emailCode.expiration}")
    private Long expiration;


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword,HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 3.调用subject方法进行登陆校验
        UsernamePasswordToken token = new UsernamePasswordToken(userAccount, userPassword);
        Subject subject = SecurityUtils.getSubject();
        // 当调用Subject.login()方法时，在shiro框架内部会去调用realm的认证方法
        subject.login(token);
        // 设置session失效时间：永不超时
        subject.getSession().setTimeout(-1001);

        // 4.登陆鉴权通过，返回当前登陆用户信息
        LoginUserVO currentUser = (LoginUserVO)subject.getPrincipal();
        return currentUser;
    }

    @Override
    public void userLogout() {
        // ShiroUtil.deleteCache();

        // 退出登陆
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            // 销毁SESSION(清理权限缓存)
            subject.logout();
        }
    }

    @Override
    public boolean sendEmailCode(String email) {
        /*
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // todo 查看注册邮箱是否存在

        // 获取发送邮箱验证码的HTML模板
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email-code.ftl");

        // 从redis缓存中尝试获取验证码
        Object code = valueOperations.get(email);
        if (code == null) {
            // 如果在缓存中未获取到验证码，则产生6位随机数，放入缓存中
            code = RandomUtil.randomNumbers(6);
            try {
                valueOperations.set(email, code, expiration, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"后台缓存服务异常");
            }
        }
        */

        // 获取发送邮箱验证码的HTML模板(resources/templates/下存放模板信息)
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email-code.ftl");

        // 调用邮箱服务发送验证码信息
        String subject = "邮箱验证码";
        String content = template.render(Dict.create().set("code", RandomUtil.randomNumbers(6)).set("expiration", expiration));
        emailService.sendMail(email,subject,content);
        // 返回响应信息
        return true;
    }
}
