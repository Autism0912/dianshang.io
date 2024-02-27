package com.hxmall.shop.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 16:58
 * @email 815835618@qq.com
 */
@Data
public class SkuReductionTo {
    private Long skuId;

    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
