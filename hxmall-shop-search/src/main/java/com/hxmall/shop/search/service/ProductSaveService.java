package com.hxmall.shop.search.service;

import com.hxmall.shop.common.to.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/28}
 * version:1.0
 */

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
