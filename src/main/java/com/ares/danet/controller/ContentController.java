package com.ares.danet.controller;

import com.ares.danet.bean.UrlData;
import com.ares.danet.service.SpiderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ContentController {
    @Resource
    private SpiderService spiderService;
    @GetMapping("/content")
    public String content(Model modelMap, @RequestParam("id") String id) {
        String content = spiderService.content(id);
        modelMap.addAttribute("content", content);
        return "content";
    }

    @GetMapping("/index")
    public String index(Model modelMap) {
        List<UrlData> list = spiderService.index();
        modelMap.addAttribute("urlList", list);
        return "list";
    }
}
