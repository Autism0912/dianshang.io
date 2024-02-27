package com.hxmall.shop.ware.ware.openfeign;

import com.hxmall.shop.common.to.SkuInfoEntity;
import com.hxmall.shop.common.to.SkuInfoTO;
import com.hxmall.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/21}
 * version:1.0
 */
@FeignClient("hxmallShopProduct")
public interface ProductSelect {
    @GetMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long id);
}
