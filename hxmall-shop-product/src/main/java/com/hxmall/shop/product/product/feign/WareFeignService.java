package com.hxmall.shop.product.product.feign;

import com.hxmall.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/28}
 * version:1.0
 */

@FeignClient("hxmallShopWare")
public interface WareFeignService {
    /**
     * 查询sku是否有库存
     */
    @PostMapping("/ware/wareinfo/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);


}
