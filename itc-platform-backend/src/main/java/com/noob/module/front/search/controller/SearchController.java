package com.noob.module.front.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.post.model.dto.post.PostQueryRequest;
import com.noob.module.admin.post.model.vo.PostVO;
import com.noob.module.admin.post.service.PostService;
import com.noob.module.admin.user.model.dto.UserQueryRequest;
import com.noob.module.admin.user.model.vo.UserVO;
import com.noob.module.admin.user.service.UserService;
import com.noob.module.front.search.model.dto.SearchRequest;
import com.noob.module.front.search.model.entity.Picture;
import com.noob.module.front.search.model.enums.SearchTypeEnum;
import com.noob.module.front.search.model.vo.SearchVO;
import com.noob.module.front.search.service.PictureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName SearchController
 * @Description 查询接口
 * @Author holic-x
 * @Date 2024/4/27 10:57
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    /**
     * V1.根据searchText查询所有的内容
     * @param searchRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "V1.根据searchText检索所有内容")
    @PostMapping("/searchAllByText")
    public BaseResponse<SearchVO> searchAllByText(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        // 查询图片
        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);

        // 查询用户
        UserQueryRequest userQueryRequest = new UserQueryRequest();// 封装UserQueryRequest
        userQueryRequest.setUserName(searchText);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);

        // 查询文章
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);

        // 封装响应数据
        SearchVO searchVO = new SearchVO();
        searchVO.setPictureList(picturePage.getRecords());
        searchVO.setUserList(userVOPage.getRecords());
        searchVO.setPostList(postVOPage.getRecords());
        return ResultUtils.success(searchVO);
    }

    /**
     * V2.根据searchText并发处理查询方法
     * @param searchRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "V2.根据searchText检索所有内容(并发处理)")
    @PostMapping("/searchAllByTextCon")
    public BaseResponse<SearchVO> searchAllByTextCon(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        // 查询图片
        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
            Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
            return picturePage;
        });

        // 查询用户
        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();// 封装UserQueryRequest
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
            return userVOPage;
        });

        // 查询文章
        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
            return postVOPage;
        });

        // 并发操作：异步对象组合，再用一个join（相当于在这里打了一个断点阻塞，只有三个查询都结束之后才会执行下面的代码）
        CompletableFuture.allOf(pictureTask, userTask, postTask).join();

        // 封装响应数据
        try {
            Page<Picture> picturePage = pictureTask.get();
            Page<UserVO> userVOPage = userTask.get();
            Page<PostVO> postVOPage = postTask.get();
            SearchVO searchVO = new SearchVO();
            searchVO.setPictureList(picturePage.getRecords());
            searchVO.setUserList(userVOPage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            return ResultUtils.success(searchVO);
        } catch (Exception e) {
            log.error("查询异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
        }
    }

    /**
     * V3.根据searchText、type处理查询，接收指定单类的数据查询；符合门面模式，提供统一接口访问
     * @param searchRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "V3.根据searchText、type组合条件处理查询")
    @PostMapping("/searchAllByCond")
    public BaseResponse<SearchVO> searchAllByCond(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        String type = searchRequest.getSearchType();
        // 检验传入指定类型为空字符串则抛出异常
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR,"未指定查询类型");
        // 校验指定type不在指定的字符串范围内则抛出异常或者默认搜索出所有数据（结合业务场景处理）
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
//        ThrowUtils.throwIf(searchTypeEnum==null,ErrorCode.PARAMS_ERROR);
        if (searchTypeEnum == null) {
            // 查询图片
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                return picturePage;
            });

            // 查询用户
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();// 封装UserQueryRequest
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                return userVOPage;
            });

            // 查询文章
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                return postVOPage;
            });

            // 并发操作：异步对象组合，再用一个join（相当于在这里打了一个断点阻塞，只有三个查询都结束之后才会执行下面的代码）
            CompletableFuture.allOf(pictureTask, userTask, postTask).join();

            // 封装响应数据
            try {
                Page<Picture> picturePage = pictureTask.get();
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                return ResultUtils.success(searchVO);
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }

        } else {
            SearchVO searchVO = new SearchVO();
            // 根据Type类别分别处理
            switch (searchTypeEnum){
                case PICTURE:
                    // 查询图片
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                    searchVO.setPictureList(picturePage.getRecords());
                    break;
                case USER:
                    // 查询用户
                    UserQueryRequest userQueryRequest = new UserQueryRequest();// 封装UserQueryRequest
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                    searchVO.setUserList(userVOPage.getRecords());
                    break;
                case POST:
                    // 查询文章
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    searchVO.setPostList(postVOPage.getRecords());
                    break;
                default:
            }
            return ResultUtils.success(searchVO);
        }
    }

}
