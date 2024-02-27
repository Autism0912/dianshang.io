package com.hxmall.shop.search.vo;

import lombok.Data;

import java.util.List;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Data
public class SearchParam {
    /**
     * 全文检索关键字
     */
    private String keyword;
    /**
     * 品牌id
     */
    private List<Long> brandId;
    /**
     * 3级分类id
     */
    private Long catelog3Id;
    /**
     * 排序字段
     */
    private String sort;
    /**
     * 是否有库存
     */
    private Integer hasStock;
    /**
     * 价格区间
     */
    private String skuPrice;
    /**
     * 属性
     */
    private List<String> attrs;
    /**
     * 原生查询
     */
    private String _queryString;
    /**
     * 页码
     */
    private Integer pageNum=1;
}
