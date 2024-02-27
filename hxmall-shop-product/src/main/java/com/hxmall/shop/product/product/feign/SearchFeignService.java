package com.hxmall.shop.product.product.feign;

import com.hxmall.shop.common.to.SkuEsModel;
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
@FeignClient("hxmallShopSearch")
public interface SearchFeignService {
    /**
     * 保存商品信息到es中
     */
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModel);
}
