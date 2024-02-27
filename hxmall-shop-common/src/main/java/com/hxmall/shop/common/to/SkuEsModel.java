package com.hxmall.shop.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/27 10:24
 * @email 815835618@qq.com
 * {
 *   "mappings": {
 *     "properties": {
 *       "skuId": {
 *         "type": "long"
 *       },
 *       "spuId": {
 *         "type": "keyword"
 *       },
 *       "skuTitle": {
 *         "type": "text",
 *         "analyzer": "ik_smart"
 *       },
 *       "skuPrice": {
 *         "type": "keyword"
 *       },
 *       "skuImg": {
 *         "type": "keyword"
 *       },
 *       "saleCount": {
 *         "type": "long"
 *       },
 *       "hasStock": {
 *         "type": "boolean"
 *       },
 *       "hotScore": {
 *         "type": "long"
 *       },
 *       "brandId": {
 *         "type": "long"
 *       },
 *       "catalogId": {
 *         "type": "long"
 *       },
 *       "brandName": {
 *         "type": "keyword"
 *       },
 *       "brandImg": {
 *         "type": "keyword"
 *       },
 *       "catalogName": {
 *         "type": "keyword"
 *       },
 *       "attrs": {
 *         "type": "nested",
 *         "properties": {
 *           "attrId": {
 *             "type": "long"
 *           },
 *           "attrName": {
 *             "type": "keyword"
 *           },
 *           "attrValue": {
 *             "type": "keyword"
 *           }
 *         }
 *       }
 *     }
 *   }
 * }
 */
@Data
public class SkuEsModel implements Serializable{
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;


    @Data
    public static class Attrs implements Serializable {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
