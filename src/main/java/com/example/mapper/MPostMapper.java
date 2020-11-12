package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.MPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.vo.PostVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wly
 * @since 2020-06-02
 */
@Component
public interface MPostMapper extends BaseMapper<MPost> {

      IPage<PostVo> selectPosts(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);
}
