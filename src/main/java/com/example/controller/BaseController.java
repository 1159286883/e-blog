package com.example.controller;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.service.*;
import com.example.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;


public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    MPostService mpostService;

    @Autowired
    MCommentService mcommentService;

    @Autowired
    MUserService muserService;

    @Autowired
    MUserMessageService messageService;

    @Autowired
    MUserCollectionService collectionService;

    @Autowired
    MCategoryService categoryService;


    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 2);
        return new Page(pn, size);
    }

    protected AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId() {
        return getProfile().getId();
    }

}
