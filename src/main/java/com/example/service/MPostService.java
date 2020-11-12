package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.MPost;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2020-06-02
 */
public interface MPostService extends IService<MPost> {


    IPage paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) ;


}
