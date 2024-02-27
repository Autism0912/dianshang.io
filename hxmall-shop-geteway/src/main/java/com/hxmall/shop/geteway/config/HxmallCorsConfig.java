package com.hxmall.shop.geteway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * author:黄龙强
 * time:{2023/12/13}
 * version:1.0
 */
@Configuration
public class HxmallCorsConfig {
    //在gateway 中 使用的是flux

    @Bean
    public CorsWebFilter corsFilter(){
        //基于url的跨域
        UrlBasedCorsConfigurationSource source  = new UrlBasedCorsConfigurationSource();
        //配置跨域信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许跨域的头
        corsConfiguration.addAllowedHeader("*");
        //允许跨域的方式
        corsConfiguration.addAllowedMethod("*");
        //允许跨域的请求来源
        corsConfiguration.addAllowedOriginPattern("*");
        //是否携带cookies跨域
        corsConfiguration.setAllowCredentials(true);

        //任意url 都要进行跨域配置
        source.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(source);
    }
}
