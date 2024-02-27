package com.hxmall.shop.search;

import com.alibaba.fastjson.JSON;
import com.hxmall.shop.search.config.HxmallElasticSearchConfig;
import com.hxmall.shop.search.entity.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class HxmallShopSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    void indexData() throws IOException {
        //1.构建索引或者更新,指定索引
        IndexRequest indexRequest = new IndexRequest("users");
        //2.设置id
        indexRequest.id("1");//数据唯一标识
        User user = new User(1001, "男", "宋林虹");
        //3.绑定数据和请求
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //4.同步
        IndexResponse index = client.index(indexRequest, HxmallElasticSearchConfig.COMMON_OPTIONS);
        //5.获取响应的数据
        System.out.println(index);
    }

    @Test
    void SearchData() throws IOException {
        /**
         *
         */
        //1.创建检索请求
        SearchRequest newIndex = new SearchRequest("new_index");
        //2.构建检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊匹配所有记录中包含的mill信息
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //年龄分布
        TermsAggregationBuilder size = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(size);

        //平均薪资
        AvgAggregationBuilder field = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(field);

        //绑定我们检索的条件
        newIndex.source(searchSourceBuilder);

        //执行检索请求
        SearchResponse searchResponse = client.search(newIndex, HxmallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse);
        SearchHit[] hits = searchResponse.getHits().getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit);
        }

        //获取聚合的结构
        Aggregations aggregations = searchResponse.getAggregations();
        //获取年龄分布
        Terms ageAgg = aggregations.get("ageAgg");
        if (ageAgg!=null){
            ageAgg.getBuckets().forEach(bucket -> {
                System.out.println("年龄"+bucket.getKeyAsString()+ " 人数: "+ bucket.getDocCount());
            });
        }
        //获取平均薪资
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资"+balanceAvg.getValue());
    }
}
