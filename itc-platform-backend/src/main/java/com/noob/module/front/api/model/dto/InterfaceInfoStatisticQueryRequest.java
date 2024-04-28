package com.noob.module.front.api.model.dto;

import lombok.Data;

/**
 * @ClassName InterfaceInfoStatisticQueryRequest
 * @Description 获取接口统计信息查询参数
 * @Author holic-x
 * @Date 2024/4/27 23:53
 */
@Data
public class InterfaceInfoStatisticQueryRequest {

    private String userName;

    private String interfaceName;

    private String interfaceType;

    private String interfaceStatus;
}
