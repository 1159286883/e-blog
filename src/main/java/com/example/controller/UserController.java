package com.example.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.lang.Result;
import com.example.entity.MPost;
import com.example.entity.MUser;
import com.example.entity.MUserMessage;
import com.example.shiro.AccountProfile;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {


    @GetMapping("/user/home")
    public String home() {

        MUser muser = muserService.getById(getProfileId());

        List<MPost> posts = mpostService.list(new QueryWrapper<MPost>()
                .eq("user_id", getProfileId())
                // 30天内
                //.gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );

        req.setAttribute("user", muser);
        req.setAttribute("posts", posts);
        return "/user/home";
    }

    @GetMapping("/user/set")
    public String set() {
        MUser muser = muserService.getById(getProfileId());
        req.setAttribute("user", muser);

        return "/user/set";
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(MUser muser) {

        if(StrUtil.isNotBlank(muser.getAvatar())) {

            MUser temp = muserService.getById(getProfileId());
            temp.setAvatar(muser.getAvatar());
            muserService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(muser.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }

        if(StrUtil.isBlank(muser.getUsername())) {
            return Result.fail("昵称不能为空");
        }
        int count = muserService.count(new QueryWrapper<MUser>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId()));
        if(count > 0) {
            return Result.fail("改昵称已被占用");
        }

        MUser temp = muserService.getById(getProfileId());
        temp.setUsername(muser.getUsername());
        temp.setGender(muser.getGender());
        temp.setSign(muser.getSign());
        muserService.updateById(temp);

        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set#info");
    }


    @ResponseBody
    @RequestMapping("/message/num" + "" + "" + "s/")
    public Map msgNums() {

        int count = messageService.count(new QueryWrapper<MUserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );
        return MapUtil.builder("status", 0)
                .put("count", count).build();
    }


}