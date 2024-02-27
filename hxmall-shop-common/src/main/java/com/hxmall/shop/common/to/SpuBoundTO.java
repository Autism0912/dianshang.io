package com.hxmall.shop.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:44
 * @email 815835618@qq.com
 */
@Data
public class SpuBoundTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
