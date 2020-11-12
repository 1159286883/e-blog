package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.service.MPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller

public class IndexController extends BaseController {

    @Autowired
    MPostService mPostService;
    @RequestMapping({"","/"})
    public String index (){

        int pn = ServletRequestUtils.getIntParameter(req,"pn",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",2);
        Page page = new Page(pn,size);
        IPage results = mPostService.paging(page,null,null,null,null,"created");
        req.setAttribute("pageData",results);
        req.setAttribute("currentCategoryId",0);
        return  "index";
    }

}
