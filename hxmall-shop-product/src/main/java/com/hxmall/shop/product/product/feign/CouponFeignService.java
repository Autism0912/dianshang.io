package com.hxmall.shop.product.product.feign;

import com.hxmall.shop.common.to.SkuReductionTo;
import com.hxmall.shop.common.to.SpuBoundTO;
import com.hxmall.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:46
 * @email 815835618@qq.com
 */
@FeignClient("hxmallShopCoupon")
public interface CouponFeignService {

    /**
     * @RequestBody 将对象转换为json,
     * coupon在收到请求，收到时json数据，那边用@RequstBody 将json数据转为SpuBoundsEntity 对象
     *
     * 只要是json数据 数据模型是兼容的 双方服务无需使用同一个TO对象
     * @param spuBoundTO
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    @PostMapping("/coupon/skufullreduction/save")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
