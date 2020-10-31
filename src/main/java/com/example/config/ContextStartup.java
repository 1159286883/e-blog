package com.example.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.entity.MCategory;
import com.example.service.MCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import javax.servlet.ServletContext;
import java.util.List;

@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {
    @Autowired
    MCategoryService mCategoryService;
    ServletContext servletContext;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<MCategory> categories = mCategoryService.list(new QueryWrapper<MCategory>()
        .eq("status", 0)
        );
        servletContext.setAttribute("categorys", categories);

    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;

    }
}
