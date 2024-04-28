package com.noob.module.admin.user.constant;

/**
 * 用户常量
 * （存放用户常量信息，包括登录信息、用户访问权限、账号状态等）
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 默认角色
     */
    String DEFAULT_PASSWORD = "12345678";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";


    /**
     * 用户状态：激活
     */
    Integer USER_STATUS_ACTIVE = 1;


    /**
     * 用户状态：禁用
     */
    Integer USER_STATUS_FORBID = 0;


    /**
     * 用户默认头像信息
     */
    String DEFAULT_AVATAR = "https://img2.baidu.com/it/u=3442676033,4275801877&fm=253&fmt=auto&app=138&f=JPEG?w=522&h=386";


    /**
     * 用户开发者模式(0-关闭、1-开启)
     */
    Integer IS_DEVELOP_OFF = 0;

    /**
     * 用户开发者模式(0-关闭、1-开启)
     */
    Integer IS_DEVELOP_ON = 1;


    /**
     * 初始化用户积分
     */
    Integer INIT_USER_SCORE = 100;


    /**
     * 初始化用户等级
     */
    String INIT_USER_GRADE_USER = "USER";


    /**
     * 用户注册渠道(后台添加、前台注册、wx注册等方式)
     */
    String USER_REGISTER_CHANNEL_BACKEND = "BACKEND";
    String USER_REGISTER_CHANNEL_FRONTEND = "FRONTEND";
    String USER_REGISTER_CHANNEL_WX = "WX";


    // endregion
}
