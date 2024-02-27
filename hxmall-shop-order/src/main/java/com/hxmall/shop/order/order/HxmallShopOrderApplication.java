package com.hxmall.shop.order.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HxmallShopOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(HxmallShopOrderApplication.class, args);
    }

}
