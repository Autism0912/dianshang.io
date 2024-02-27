package com.hxmall.shop.product.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/12 10:15
 * @email 815835618@qq.com
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hxmall.shop.product.product.feign")
public class HxmallShopProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(HxmallShopProductApplication.class, args);
    }
}
