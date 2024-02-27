package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.util.List;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
