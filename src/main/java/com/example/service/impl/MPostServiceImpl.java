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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

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
            this.hashCachePostIdAndTitle(mpost, expireTime);
        }
        // 做并集
        this.zunionAndStoreLast7DayForWeekRank();

    }

    /**
     * 本周合并每日评论数量操作
     */
    private void zunionAndStoreLast7DayForWeekRank() {
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);

        String destKey = "week:rank";
        List<String> otherKeys = new ArrayList<>();
        for(int i=-6; i < 0; i++) {
            String temp = "day:rank:" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);

            otherKeys.add(temp);
        }

        redisUtil.zUnionAndStore(currentKey, otherKeys, destKey);
    }


    private void hashCachePostIdAndTitle(MPost mpost, long expireTime) {
        String key = "rank:post:" + mpost.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if(!hasKey) {
            redisUtil.hset(key, "post:id", mpost.getId(), expireTime);
            redisUtil.hset(key, "post:title", mpost.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", mpost.getCommentCount(), expireTime);
            redisUtil.hset(key, "post:viewCount", mpost.getViewCount(), expireTime);
        }
    }

    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr? 1: -1);

        MPost mpost = this.getById(postId);

        // 7天后自动过期(15号发表，7-（18-15）=4)
        long between = DateUtil.between(new Date(), mpost.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

        // 缓存这篇文章的基本信息
        this.hashCachePostIdAndTitle(mpost, expireTime);

        // 重新做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    @Override
    public void putViewCount(PostVo vo) {
        String key = "rank:post:" + vo.getId();


        // 1、从缓存中获取viewcount
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");

        // 2、如果没有，就先从实体里面获取，再加一
        if(viewCount != null) {
            vo.setViewCount(viewCount + 1);
        } else {
            vo.setViewCount(vo.getViewCount() + 1);
        }

        // 3、同步到缓存里面
        redisUtil.hset(key, "post:viewCount", vo.getViewCount());

    }

}
