package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.MPost;
import com.example.mapper.MPostMapper;
import com.example.service.MPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wly
 * @since 2020-06-02
 */
@Service
public class MPostServiceImpl extends ServiceImpl<MPostMapper, MPost> implements MPostService {

    @Autowired
    MPostMapper mPostMapper;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order){

        if(level==null) level=-1;
        QueryWrapper wrapper = new QueryWrapper<MPost>()
        .eq(categoryId!=null, "categor_id",categoryId)
                .eq(userId!=null, "user_id",userId)
                .eq(level==0, "level",0)
                .gt(level>0,"level",0)
                .orderByDesc(order!=null, order);
        return mPostMapper.selectPosts(page,wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<MPost> wrapper) {

        return mPostMapper.selectOnePost(wrapper);
    }
}
