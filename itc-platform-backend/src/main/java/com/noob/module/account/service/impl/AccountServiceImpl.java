package com.noob.module.account.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.noob.framework.cache.RedisCache;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.mapper.UserMapper;
import com.noob.module.admin.base.user.model.entity.User;
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
import java.util.Date;

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

    @Resource
    private RedisCache redisCache;

    @Value("${custom.emailCode.expiration}")
    private Long expiration;

    @Resource
    private UserMapper userMapper;


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
        // 校验当前指定的邮箱是否已被绑定（查看注册邮箱是否存在）
//        User findUser = userMapper.getUserVOByEmail(email);
//        ThrowUtils.throwIf(findUser != null,ErrorCode.USER_EMAIL_REPEAT_ERROR,"当前邮箱账号已被注册，请确认后再次尝试");

        // 定义存储键值对的键规则
        String emailCodeKey = "emailCode:" + email;

        // 从redis缓存中尝试获取验证码
        String cacheCode = redisCache.getCacheObject(emailCodeKey);
        String emailCode = "";
        // 如果缓存中已经存在字符串数据，则直接取出
        if(StringUtils.isBlank(cacheCode)){
            // 随机生成6位验证码
            emailCode = RandomUtil.randomNumbers(6);
            // 将邮箱和验证码信息存入redis缓存
            redisCache.setCacheObject(emailCodeKey,emailCode);
            redisCache.expire(emailCodeKey,expiration*100);
        }else{
            // 缓存中已经存在数据，不重复生成，直接返回缓存数据（或者提示用户不要操作太频繁，确认邮箱后再重新尝试）
            // emailCode = cacheCode;
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST,"验证码已生成并发送到您邮箱，请确认后再次尝试");
        }

        // 获取发送邮箱验证码的HTML模板(resources/templates/下存放模板信息)
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email-code.ftl");

        // 调用邮箱服务发送验证码信息
        String subject = "【一人の境】邮箱验证码";
        String content = template.render(Dict.create().set("code",emailCode).set("expiration", expiration));
        emailService.sendMail(email,subject,content);
        // 返回响应信息
        return true;
    }

    /**
     * 根据验证码、邮箱绑定当前用户邮箱信息
     * @param email
     * @param code
     * @return
     */
    @Override
    public boolean bindEmail(String email, String code) {
        // 校验当前指定的邮箱是否已被绑定（查看注册邮箱是否存在）
        User findUser = userMapper.getUserVOByEmail(email);
        ThrowUtils.throwIf(findUser != null,ErrorCode.USER_EMAIL_REPEAT_ERROR,"当前邮箱账号已被他人绑定，请确认后再次尝试");

        // 获取缓存中的验证码信息
        String emailCodeKey = "emailCode:" + email;
        String cacheCode = redisCache.getCacheObject(emailCodeKey);
        ThrowUtils.throwIf(StringUtils.isBlank(cacheCode),ErrorCode.VALID_CODE_ERROR,"当前验证码已过期，请稍后再次尝试");

        // 校验验证码是否正确
        ThrowUtils.throwIf(!cacheCode.equals(code),ErrorCode.VALID_CODE_ERROR,"验证码校验失败，请确认后再次尝试");

        // 邮箱账号和验证码校验通过，更新邮箱信息
        User user = new User();
        user.setId(ShiroUtil.getCurrentUserId());
        user.setUserEmail(email);
        user.setUpdateTime(new Date());
        int res = userMapper.updateById(user);
        return res>0;
    }
}
