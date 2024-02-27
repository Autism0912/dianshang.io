package com.hxmall.shop.coupon.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HxmallShopCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(HxmallShopCouponApplication.class, args);
    }

}
