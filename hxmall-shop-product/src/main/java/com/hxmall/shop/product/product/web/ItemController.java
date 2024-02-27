package com.hxmall.shop.product.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Controller
public class ItemController {
    @GetMapping("/{skuId}.html")
    public String itemPage(@PathVariable("skuId") Long skuId, Model model){
        return "item";
    }
}
