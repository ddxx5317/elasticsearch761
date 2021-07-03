package com.ddxx.es7.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/3
 */
@RestController
public class IndexController {

    @GetMapping("/index")
    public String index(){
        return "SUCCESS";
    }
}
