package com.hxmall.shop.search.controller;

import com.hxmall.shop.common.constant.ProductConstant;
import com.hxmall.shop.common.enume.BizCodeEnum;
import com.hxmall.shop.common.to.SkuEsModel;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/28}
 * version:1.0
 */
@RestController
@Slf4j
@RequestMapping("/search/save")
public class ElasticSaveController {
    private final ProductSaveService productSaveService;

    public ElasticSaveController(ProductSaveService productSaveService) {
        this.productSaveService = productSaveService;
    }

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        log.info("商品上架了,准备上架到es中");
        Boolean status;
        try {
           status = productSaveService.productStatusUp(skuEsModels);
        }catch (IOException e){
            log.error("商品上架失败",e);
            return R.error(ProductConstant.productStatus.UP_FAILL.getCode()
                    ,ProductConstant.productStatus.UP_FAILL.getMsg());
        }
        if (!status){
            return R.ok();
        }
        return R.error(ProductConstant.productStatus.UP_FAILL.getCode()
                ,ProductConstant.productStatus.UP_FAILL.getMsg());
    }
}
