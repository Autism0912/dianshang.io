package com.hxmall.shop.search.service.imp;

import com.alibaba.fastjson2.JSON;
import com.hxmall.shop.common.to.SkuEsModel;
import com.hxmall.shop.search.constant.EsConstant;
import com.hxmall.shop.search.config.HxmallElasticSearchConfig;
import com.hxmall.shop.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author:黄龙强
 * time:{2023/12/28}
 * version:1.0
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    private  final RestHighLevelClient client;

    public ProductSaveServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //1.批量保存
        BulkRequest bulkRequest = new BulkRequest();
        //2.构造保存请求
        for (SkuEsModel skuEsModel : skuEsModels) {
            //3.构造请求体
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //设置索引的id
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            //添加文档
            bulkRequest.add(indexRequest);
        }
        //批量保存
        BulkResponse bulk = client.bulk(bulkRequest, HxmallElasticSearchConfig.COMMON_OPTIONS);
        //返回结果有没有问题
        boolean b = bulk.hasFailures();
        if (b){
            List<String> collect
                    = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());
            log.error("商品上架错误,{}",collect);
        }
        return b;
    }
}
