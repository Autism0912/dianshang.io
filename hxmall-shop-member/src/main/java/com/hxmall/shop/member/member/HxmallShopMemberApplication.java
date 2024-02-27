package com.hxmall.shop.member.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient //监听设备上线和下线(状态)
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hxmall.shop.member.member.feign")
public class HxmallShopMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(HxmallShopMemberApplication.class, args);
    }

}
