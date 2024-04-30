package com.noob.module.admin.bi.controller;

import cn.hutool.core.io.FileUtil;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.framework.utils.ExcelUtils;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.module.admin.bi.bizmq.BiMessageProducer;
import com.noob.module.admin.bi.manager.AiManager;
import com.noob.module.admin.bi.manager.RedisLimiterManager;
import com.noob.module.admin.bi.model.constant.BiConstant;
import com.noob.module.admin.bi.model.dto.*;
import com.noob.module.admin.bi.model.entity.Chart;
import com.noob.module.admin.bi.model.vo.BiResponse;
import com.noob.module.admin.bi.service.AnalysicService;
import com.noob.module.admin.bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.util.AnalysisSPILoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 图表接口
 */
@RestController
@RequestMapping("/bi/analysic")
@Slf4j
public class AnalysicController {

    @Resource
    private ChartService chartService;

    @Resource
    private AnalysicService analysicService;


    /**
     * 智能分析(MQ)
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    @PostMapping("/genChartByAiAsyncMq")
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        BiResponse biResponse = analysicService.genChartByAiAsyncMq(multipartFile,genChartByAiRequest);
        return ResultUtils.success(biResponse);
    }



    /**
     * 智能分析（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    @PostMapping("/genChartByAiAsync")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        BiResponse biResponse = analysicService.genChartByAiAsync(multipartFile,genChartByAiRequest);
        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析接口（同步：校验=》限流=》构造用户输入、调用AI）
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    @PostMapping("/genChartByAiSync")
    public BaseResponse<BiResponse> genChartByAiSync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        BiResponse biResponse = analysicService.genChartByAiSync(multipartFile,genChartByAiRequest);
        return ResultUtils.success(biResponse);
    }

    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和原始数据，请告诉我分析结论。");
        userInput.append("分析目标：").append(goal).append("\n");

        // 压缩后的数据
        String result = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("数据：").append(result).append("\n");
        return ResultUtils.success(userInput.toString());
    }
     */

    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 测试读取文件
        String result = ExcelUtils.excelToCsv(multipartFile);
        return ResultUtils.success(result);
    }
     */
    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 读取用户上传的excel文件，进行处理
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        File file = null;
        try {
            // 返回可访问地址
            return ResultUtils.success("");
        } catch (Exception e) {
//            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
//                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    */

}
