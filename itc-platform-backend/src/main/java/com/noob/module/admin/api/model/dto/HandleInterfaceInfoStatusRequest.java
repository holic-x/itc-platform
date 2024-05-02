package com.noob.module.admin.api.model.dto;

import lombok.Data;

/**
 * @ClassName HandleInterfaceInfoStatusRequest
 * @Description TODO
 * @Author holic-x
 * @Date 2024/4/29 19:16
 */
@Data
public class HandleInterfaceInfoStatusRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 接口状态
     */
    private int status;

    private static final long serialVersionUID = 1L;
}
