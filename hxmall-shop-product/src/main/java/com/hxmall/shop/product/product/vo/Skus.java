package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Description sku信息
 * @Author ghl
 * @Date 2023/12/20 15:11
 * @email 815835618@qq.com
 */
@Data
public class Skus implements Serializable {
    private List<Attr> attr;

    private String skuName;

    private BigDecimal price;

    private String skuTitle;

    private String skuSubtitle;

    private List<Images> images;

    private List<String> descar;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    /**
     * 满减价格
     */

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;

}
