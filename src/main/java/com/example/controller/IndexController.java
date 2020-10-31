package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.entity.BaseEntity;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.jvm.hotspot.debugger.Page;


@Controller

public class IndexController extends BaseController {

    @RequestMapping({"","/"})
    public String index (){

        int pn = ServletRequestUtils.getIntParameter(req,"pn",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",5);
        Page page = new Page(pn,size);
        IPage result = mPostService.paging();

        req.setAttribute("currentCategoryId",0);
        return  "index";
    }

}
