package com.ddxx.es7.controller;

import com.ddxx.es7.service.ContentService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/3
 */
@RestController
@Slf4j
public class ContentController {

    @Resource
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword")String keyword) throws Exception{
        return contentService.parse(keyword);
    }

    @GetMapping("/searchPage/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> parse(@PathVariable("keyword")String keyword,
                                          @PathVariable("pageNo") int pageNo,
                                          @PathVariable("pageSize") int pageSize) throws Exception{
        log.info("keyword={},pageNo={},pageSize={}", keyword,pageNo,pageSize);
        return contentService.searchPage(keyword,pageNo,pageSize);
    }
}
