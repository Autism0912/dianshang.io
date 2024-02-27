package com.hxmall.shop.search.feign;

import com.hxmall.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@FeignClient("hxmallShopProduct")
public interface ProductFeignService {
    @RequestMapping("/product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    R brandInfo(@RequestParam("brandIds") List<Long> brandIds);
}
