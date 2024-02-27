package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:19
 * @email 815835618@qq.com
 */
@Data
public class MemberPrice implements Serializable {

    private Long id;
    private String name;
    private BigDecimal price;
}
