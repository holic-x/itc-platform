package com.noob.module.admin.bi.service.impl;

import cn.hutool.core.io.FileUtil;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.framework.utils.ExcelUtils;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.bi.bizmq.BiMessageProducer;
import com.noob.module.admin.bi.manager.AiManager;
import com.noob.module.admin.bi.manager.RedisLimiterManager;
import com.noob.module.admin.bi.model.constant.BiConstant;
import com.noob.module.admin.bi.model.dto.GenChartByAiRequest;
import com.noob.module.admin.bi.model.entity.Chart;
import com.noob.module.admin.bi.model.enums.ChartStatusEnum;
import com.noob.module.admin.bi.model.vo.BiResponse;
import com.noob.module.admin.bi.service.AnalysicService;
import com.noob.module.admin.bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName AnalysicServiceImpl
 * @Description TODO
 * @Author Huh-x
 * @Date 2024 2024/4/30 18:08
 */
@Slf4j
@Service
public class AnalysicServiceImpl implements AnalysicService {

    @Resource
    private ChartService chartService;

    // ai服务
    @Resource
    private AiManager aiManager;

    // 接口限流器
    @Resource
    private RedisLimiterManager redisLimiterManager;

    // 异步处理线程池(自动注入一个线程池实例)
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    // 引入消息生产者
    @Resource
    private BiMessageProducer biMessageProducer;

    // BI模型ID
    private final long biModelId = BiConstant.BI_MODEL_ID;

    @Override
    public void validParam(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 基本参数校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        /**
         * 文件信息校验（校验文件后缀、大小等基本信息，防止文件上传漏洞攻击）
         */
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小，如果文件大于1兆则抛出异常，提示文件超过1M
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR,"文件超过1M");
        // 检验文件后缀（一般是xxx.csv，获取到.后缀），可借助FileUtil工具类的getSuffix方法获取
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx","xls"); // ".png",".csv",".jpg",".svg","webp","jpeg"
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀格式非法");

    }

    private String buildUserInput(String goal,String chartType,MultipartFile multipartFile){
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }


    /**
     * 处理更新图表执行中状态失败方法
     * @param chartId
     * @param execMessage
     */
    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updatedChartResult = new Chart();
        updatedChartResult.setId(chartId);
        updatedChartResult.setStatus("failed");
        updatedChartResult.setExecMessage(execMessage);
        boolean updatedResOp = chartService.updateById(updatedChartResult);
        if(!updatedResOp){
            log.error("更新图表【失败】状态失败"+chartId+","+execMessage);
        }
    }

    // 智能分析接口（同步：校验=》限流=》构造用户输入、调用AI）
    @Override
    public BiResponse genChartByAiSync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 获取当前登陆用户
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();

        // 调用公共方法校验参数正确性
        validParam(multipartFile,genChartByAiRequest);

        // 引入限流判断(根据用户ID进行判断)
        redisLimiterManager.doRateLimit("genChartByAi_"+currentUser.getId());

        /** -------------------- start AI智能分析流程 ----------------------- **/
        // 1.调用方法构造用户输入
        String userInput = buildUserInput(goal,chartType,multipartFile);

        // 2.调用AI获取AI结果（这个过程涉及第三方AI交互，比较慢）
        String result = aiManager.doChat(biModelId, userInput);

        // 3.解析交互结果，并将数据存储到本地数据库中
        // 此处分隔符以设定为参考 【【【【【
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 封装数据，并插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        // 获取压缩后的文件数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(currentUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        /** -------------------- end AI智能分析流程 ----------------------- **/

        // 封装交互响应结果
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    @Override
    public BiResponse genChartByAiAsync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 获取当前登陆用户
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();

        // 调用公共方法校验参数正确性
        validParam(multipartFile,genChartByAiRequest);

        // 引入限流判断(根据用户ID进行判断)
        redisLimiterManager.doRateLimit("genChartByAi_"+currentUser.getId());


        // --------------------------------- start 异步处理逻辑 ------------------------------
        // 1.先将图标数据保存到数据库中
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        // 获取压缩后的文件数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        // 数据库插入时，还没生成结果（先插入数据的基础信息，异步调用AI接口生成图表信息），将图表任务状态设置为排队中
        chart.setStatus(ChartStatusEnum.WAIT.getValue());
        chart.setUserId(currentUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // 2.在最终的返回结果前提交一个任务（todo 建议处理任务队列满了后，抛异常的情况，因为提交任务报错了，前端会返回异常）
        CompletableFuture.runAsync(()->{
            // 2.1 修改图表任务状态为执行中
            Chart updatedChart = new Chart();
            updatedChart.setId(chart.getId());
            updatedChart.setStatus("running");
            boolean updatedOp = chartService.updateById(updatedChart);
            if(!updatedOp){
                handleChartUpdateError(chart.getId(),"更新图表【执行中】状态失败");
                return ;
            }
            // 2.2 异步调用AI接口
            // 调用方法构造用户输入
            String userInput = buildUserInput(goal,chartType,multipartFile);
            String result = aiManager.doChat(biModelId, userInput);
            // 此处分隔符以设定为参考
            String[] splits = result.split("【【【【【"); // 【【【【【
            if (splits.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();

            // 2.3 AI结果调用成功后，更新任务状态，并将AI结果进行封装
            Chart updatedChartResult = new Chart();
            updatedChartResult.setId(chart.getId());
            updatedChartResult.setGenChart(genChart);
            updatedChartResult.setGenResult(genResult);
            updatedChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
            boolean updatedResOp = chartService.updateById(updatedChartResult);
            if(!updatedResOp){
                handleChartUpdateError(chart.getId(),"更新图表【成功】状态失败");
            }
        },threadPoolExecutor);
        // --------------------------------- end 异步处理逻辑 ------------------------------
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    @Override
    public BiResponse genChartByAiAsyncMq(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 获取当前登陆用户
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();

        // 调用公共方法校验参数正确性
        validParam(multipartFile,genChartByAiRequest);

        // 引入限流判断(根据用户ID进行判断)
        redisLimiterManager.doRateLimit("genChartByAi_"+currentUser.getId());

        // --------------------------------- start 异步处理逻辑 ------------------------------
        // 1.先将图标数据保存到数据库中
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        // 获取压缩后的文件数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        // 数据库插入时，还没生成结果（先插入数据的基础信息，异步调用AI接口生成图表信息），将图表任务状态设置为排队中
        chart.setStatus(ChartStatusEnum.WAIT.getValue());
        chart.setUserId(currentUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // 2.在最终的返回结果前发送MQ消息(调用图表AI的过程由生产者派发任务进行处理)
        biMessageProducer.sendMessage(String.valueOf(chart.getId()));

        // --------------------------------- end 异步处理逻辑 ------------------------------
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return biResponse;
    }
}
