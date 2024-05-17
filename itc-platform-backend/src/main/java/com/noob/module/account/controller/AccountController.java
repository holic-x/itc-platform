package com.noob.module.account.controller;

import cn.hutool.core.io.FileUtil;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.model.dto.UserLoginRequest;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.model.vo.UserSignVO;
import com.noob.module.admin.base.user.model.vo.UserVO;
import com.noob.module.admin.base.user.service.UserExtendService;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.module.admin.base.user.service.UserSignService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @Description 账号相关控制器
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:14
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private UserService userService;

    @Resource
    private UserSignService userSignService;

    @Resource
    private UserExtendService userExtendService;


    @Value("${txCloud.dev-COS.secretId}")
    private String secretId;

    @Value("${txCloud.dev-COS.secretKey}")
    private String secretKey;

    @Value("${txCloud.dev-COS.appId}")
    private String appId;

    @Value("${txCloud.dev-COS.bucket}")
    private String bucket;

    // cos客户端定义
    private COSClient cosClient;


    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用登陆验证方法（经Shiro机制处理）
        LoginUserVO loginUserVO = accountService.userLogin(userAccount, userPassword, request);

        // 返回登陆成功的用户信息
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 用户注销
     *
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        // 调用登陆退出方法
        accountService.userLogout();
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getCurrentLoginUser() {
        // 获取当前登陆用户信息（基于shiro获取）
        Subject subject = SecurityUtils.getSubject();
        LoginUserVO currentUser = (LoginUserVO) subject.getPrincipal();
        return ResultUtils.success(currentUser);
    }

    /**
     * 获取用户信息详情（获取当前登录用户）
     * @return
     */
    @GetMapping("/getUserVOMoreByCurrentLoginUser")
    public BaseResponse<UserVO> getUserVOMoreByCurrentLoginUser() {
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        // 获取用户信息详情
        return ResultUtils.success(userService.getUserVOMore(currentUser.getId()));
    }


    /**
     * 用户签到
     * @return
     */
    @PostMapping("/userSignIn")
    public BaseResponse<Long> userSignIn() {

        // 校验当前用户当天是否已签到（禁止重复签到）
        UserSignVO userSignVO = userSignService.validCurrentUserSign();
        ThrowUtils.throwIf(userSignVO != null, ErrorCode.OPERATION_ERROR,"用户当日已签到，请勿重复操作");

        UserSign userSign = new UserSign();
        userSign.setUid(ShiroUtil.getCurrentUserId());
        userSign.setTitle("签到");
        userSign.setSignInChannel("web");
        userSign.setSignInTime(new Date());
        // 默认签到默认添加10积分
        userSign.setScore(10);

        // 新增签到记录
        boolean result = userSignService.save(userSign);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"签到记录新增失败");

        // 修改用户积分
        boolean updateUserScoreRes = userExtendService.updateUserScore(ShiroUtil.getCurrentUserId(),10,UserConstant.USER_SCORE_OPER_TYPE_ADD);
        ThrowUtils.throwIf(!updateUserScoreRes, ErrorCode.OPERATION_ERROR,"用户积分更新失败");

        // 返回签到记录ID
        return ResultUtils.success(userSign.getId());
    }


    /**
     * 发送邮箱验证码
     * @return
     */
    @GetMapping("/sendEmailCode")
    public BaseResponse<Boolean> sendEmailCode(@RequestParam String email) {
        // 调用验证码服务获取邮箱验证码信息
        boolean res = accountService.sendEmailCode(email);
        return ResultUtils.success(res);
    }


    /**
     * 换绑邮箱
     * @return
     */
    @GetMapping("/bindEmail")
    public BaseResponse<Boolean> bindEmail(@RequestParam String email,@RequestParam String code) {
        // 调用验证码服务获取邮箱验证码信息
        boolean res = accountService.bindEmail(email,code);
        return ResultUtils.success(res);
    }


    /**
     * 更换头像
     */
    @PostMapping("/uploadAvatar")
    public BaseResponse<String> uploadAvatar(@RequestPart("avatar") MultipartFile multipartFile) throws IOException {

        // 存储当前用户头像信息，并返回在COS中存储的数据

        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
        // 生成cos客户端
        cosClient = new COSClient(cred, clientConfig);

        // 指定文件将要存放的存储桶
        String bucketName = bucket + "-" + appId;


        // 对象key（存储桶的唯一标识）：指定文件上传到 COS 上的路径，即对象键。例如此处设定表示将文件 logo.jpg 上传到 folder 路径下

        // 获取文件的后缀名
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());

        // 文件命名规则：当前登录用户ID命名文件夹
        long currentUserId = ShiroUtil.getCurrentUserId();
        String key = "/itc-platform/personalInfo/" + currentUserId + "/avatar." + fileSuffix;

        // 需要将multipartFile临时存储，然后调整
        String originalFilename = multipartFile.getOriginalFilename();
        String prefix = originalFilename.split("\\.")[0];
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);

        // 上传文件到COS
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        // 执行文件上传操作
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        System.out.println(putObjectResult);

        // 返回上传的文件url

//        return ResultUtils.success(putObjectResult.getETag());

            // 返回拼接的文件Key
        String domain = "https://example-bucket-1305292171.cos.ap-guangzhou.myqcloud.com";
        String fileKey = domain + key;




        // 文件上传完成，需要相应更新用户信息
        User upodateUser = new User();
        upodateUser.setId(currentUserId);
        upodateUser.setUserAvatar(fileKey);
        ThrowUtils.throwIf(!userService.updateById(upodateUser),ErrorCode.PARAMS_ERROR,"用户头像信息更新失败");

        // 返回上传的文件key
        return ResultUtils.success(fileKey);

    }


}
