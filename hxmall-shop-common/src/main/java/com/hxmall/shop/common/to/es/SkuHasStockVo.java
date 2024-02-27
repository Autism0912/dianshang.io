package com.hxmall.shop.common.to.es;

import lombok.Data;

/**
 * @version 1.0
 * @Description 是否有库存的vo
 * @Author ghl
 * @Date 2023/12/28 10:06
 * @email 815835618@qq.com
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    /*指定的sku是否有库存*/
    private Boolean hasStock;
}
