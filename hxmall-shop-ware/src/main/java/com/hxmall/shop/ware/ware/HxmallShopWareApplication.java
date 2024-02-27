package com.hxmall.shop.ware.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hxmall.shop.ware.ware.openfeign")
public class HxmallShopWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HxmallShopWareApplication.class, args);
    }

}
