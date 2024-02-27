package com.hxmall.shop.search.controller;


import com.hxmall.shop.search.service.SearchService;
import com.hxmall.shop.search.vo.SearchParam;
import com.hxmall.shop.search.vo.SearchResult;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Controller
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/list.html")
    public String ListPage(SearchParam searchParam, Model model, HttpServletRequest request){
        //获取原生查询属性
        searchParam.set_queryString(request.getQueryString());
        //从es中检索到结果,传递给页面
       SearchResult searchResult = searchService.search(searchParam);
       model.addAttribute("result",searchResult);
       return "list";
    }

}
