package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.util.List;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Data
public class SpuItemAttrGroupVo {
    private String attrGroupName;
    private List<SpuBaseAttrVo> attrs;
}
