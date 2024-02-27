package com.hxmall.shop.product.product.web;

import com.hxmall.shop.product.product.entity.CategoryEntity;
import com.hxmall.shop.product.product.service.CategoryService;
import com.hxmall.shop.product.product.vo.Catelog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * author:黄龙强
 * time:{2023/12/28}
 * version:1.0
 */
@Controller
public class ProductController {
    private final CategoryService categoryService;

    public ProductController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({"/","/index.html","index"})
    public String toIndex(Model model){

        //页面初始化需要将分类信息加载出来
       List<CategoryEntity> categoryEntities = categoryService.getLevelCategorys();
       model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatalogJson(){
    Map<String,List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
    return catalogJson;
    }
}
