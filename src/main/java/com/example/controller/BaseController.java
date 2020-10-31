package com.example.controller;



import com.example.service.MPostService;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;


public class BaseController {
@Autowired
    HttpServletRequest req;

    @Autowired
    MPostService mPostService;
}
