package com.noob.module.front.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.front.search.model.entity.Picture;

/**
 * @ClassName PictureService
 * @Description 图片服务
 * @Author holic-x
 * @Date 2024/4/27 11:01
 */
public interface PictureService {
    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);

}
