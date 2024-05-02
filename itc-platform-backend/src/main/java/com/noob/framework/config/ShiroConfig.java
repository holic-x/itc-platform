package com.noob.framework.config;

import com.noob.framework.realm.UserRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description Shiro全局配置
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:10
 */

@Configuration
public class ShiroConfig {

    /**
     * 配置安全管理器
     *
     * @param userRealm UserRealm
     * @return DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(userRealm);

        return securityManager;
    }

    /**
     * 配置Shiro过滤器工厂
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 注册安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /*
         * 设置登录页面的地址
         * 当用户访问认证资源的时候，如果用户没有登录，那么就会跳转到指定的页面
         */
        shiroFilterFactoryBean.setLoginUrl("/login.html");

        // 定义资源访问规则
        Map<String, String> map = new LinkedHashMap<>();

        /*
         * 过滤器说明
         * anon：不需要认证就可以访问的资源
         * authc：需要登录认证才能访问的资源
         */
        map.put("/html/home.html", "authc");

        // 不需要认证就能访问（可配置静态资源或接口访问）
        map.put("/login.html", "anon");

        map.put("/account/login", "anon");
        map.put("/account/logout", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }


}
