package com.example.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.MPost;
import com.example.mapper.MPostMapper;
import com.example.service.MPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.util.RedisUtil;
import com.example.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Autowired
    RedisUtil redisUtil;


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

    /*本周热议初始化*/
    @Override
    public void initWeekRank(){
        // 获取7天的发表的文章
        List<MPost> mposts = this.list(new QueryWrapper<MPost>()
                .ge("created", DateUtil.offsetDay(new Date(), -7)) //7天前的日期
                .select("id, title, user_id, comment_count, view_count, created")
        );

        // 初始化文章的总评论量
        for (MPost mpost : mposts) {
            String key = "day:rank:" + DateUtil.format(mpost.getCreated(), DatePattern.PURE_DATE_FORMAT);

            redisUtil.zSet(key, mpost.getId(), mpost.getCommentCount());

            // 7天后自动过期(15号发表，7-（18-15）=4)
            long between = DateUtil.between(new Date(), mpost.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

            redisUtil.expire(key, expireTime);


            // 缓存文章的一些基本信息（id，标题，评论数量，作者）
            //this.hashCachePostIdAndTitle(mpost, expireTime);
        }


        // 做并集
    };
}
