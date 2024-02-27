package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version 1.0
 * @Description 优惠信息vo
 * @Author ghl
 * @Date 2023/12/20 15:07
 * @email 815835618@qq.com
 */
@Data
public class Bounds implements java.io.Serializable{
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
