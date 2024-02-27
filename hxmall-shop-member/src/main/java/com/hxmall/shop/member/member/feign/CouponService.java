package com.hxmall.shop.member.member.feign;


import com.hxmall.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * author:黄龙强
 * time:{2023/12/12}
 * version:1.0
 */
@FeignClient("hxmallShopCoupon")
public interface CouponService {

    //指定需要远程调用的接口的url
    @GetMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
