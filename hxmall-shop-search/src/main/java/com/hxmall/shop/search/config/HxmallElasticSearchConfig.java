package com.hxmall.shop.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author:黄龙强
 * time:{2023/12/26}
 * version:1.0
 */
@Configuration
public class HxmallElasticSearchConfig {

    /**
     * 添加安全访问规则,需要使用RequestOptions进行封装
     * @return
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS=builder.build();
    }

    @Bean
   public RestHighLevelClient escClient(){
       RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
               RestClient.builder(
                       new HttpHost("192.168.56.101",9200,"http")
               )
       );
       return restHighLevelClient;
   }
}
