package com.noob.module.admin.bi.service;

import com.noob.module.admin.bi.model.dto.GenChartByAiRequest;
import com.noob.module.admin.bi.model.vo.BiResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName Analysis
 * @Description TODO
 * @Author Huh-x
 * @Date 2024 2024/4/30 18:02
 */
public interface AnalysicService {

    /**
     * 校验参数正确性
     * @param multipartFile
     * @param genChartByAiRequest
     */
    void validParam(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);


    /**
     * 方案1：同步处理
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    BiResponse genChartByAiSync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

    /**
     * 方案2：异步处理（线程池）
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    BiResponse genChartByAiAsync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

    /**
     * 方案3：引入MQ任务编排
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    BiResponse genChartByAiAsyncMq(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

}
