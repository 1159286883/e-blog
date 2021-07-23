package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.service.MPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller

public class IndexController extends BaseController {
    @Autowired
    MPostService mPostService;
    @RequestMapping({"","/"})
    public String index (){

        IPage results = mPostService.paging(getPage(),null,null,null,null,"created");
        req.setAttribute("pageData",results);
        req.setAttribute("currentCategoryId",0);
        return  "index";
    }
    @RequestMapping({"/1"})
    public String index1 (){

        IPage results1 = mPostService.paging1(getPage(),null,null,null,null,"created");
        req.setAttribute("pageData1",results1);
        req.setAttribute("currentCategoryId",0);
        return  "index1";
    }

}
