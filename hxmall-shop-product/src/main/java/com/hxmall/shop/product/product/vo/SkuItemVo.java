package com.hxmall.shop.product.product.vo;

import com.hxmall.shop.product.product.entity.SkuImagesEntity;
import com.hxmall.shop.product.product.entity.SkuInfoEntity;
import com.hxmall.shop.product.product.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Data
public class SkuItemVo {
    private SkuInfoEntity info;
    private boolean hasStock = true;
    private List<SkuImagesEntity> images;
    private List<SkuItemSaleAttrVo> saleAttr;
    private SpuInfoEntity desc;
    private List<SpuItemAttrGroupVo> baseAttr;
}
