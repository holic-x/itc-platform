package com.noob.framework.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.BusinessException;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.mapper.UserMapper;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName Huh-x
 * @Description Shiro-用户认证
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:09
 */
@Component
@Slf4j
public class UserRealm extends AuthorizingRealm {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "noob";

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    /**
     * 认证回调函数-用户身份认证,在登录时触发调用
     **/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 获取登陆用户信息
        String userAccount = token.getUsername();
        String userPassword = new String(token.getPassword());

        // 根据用户账号获取到登陆用户信息，账号需加密后进行校验
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("用户登陆失败，用户名或密码验证失败");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }else{
            // 校验账号状态
            if(UserConstant.USER_STATUS_FORBID==user.getUserStatus()){
                throw new BusinessException(ErrorCode.USER_STATUS_FORBID_ERROR);
            }
        }

        // todo：多账号重复问题处理





        // 将用户信息封装为登陆用户信息
        LoginUserVO currentUser = new LoginUserVO();
        BeanUtils.copyProperties(user, currentUser);

        // 返回认证成功的用户信息

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                 currentUser,        // 传入整个登录用户实体对象随后权限认证的时候便可通过(T)principalCollection.getPrimaryPrincipal()访问
                 userPassword,    // 密码
                 //  ByteSource.Util.bytes(user.getSalt()), //设置盐值
                 getName()
         );

        return authenticationInfo;
    }

    /**
     * 授权查询回调函数-进行鉴权但缓存中无用户的授权信息时调用
     * 用户进行权限验证时候Shiro会去缓存中找,如果查不到数据,会执行这个方法去查权限,并放入缓存中
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        // 授权
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 获取到当前登陆用户信息
        LoginUserVO loginUser = (LoginUserVO) principals.getPrimaryPrincipal();
        // 自定义授权处理(角色、权限设定)
        Set<String> roleSet = new HashSet<>();
        // 用户角色设定：管理员、用户等
        roleSet.add(loginUser.getUserRole());
        log.info("shiro-{}-授权认证:用户ID:{}-用户名称:{}-角色列表{}", getName(),
                loginUser.getId(),loginUser.getUserName(), roleSet);
        authorizationInfo.setRoles(roleSet);
        return authorizationInfo;
    }

}
