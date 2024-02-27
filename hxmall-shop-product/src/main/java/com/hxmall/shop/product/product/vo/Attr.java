package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:13
 * @email 815835618@qq.com
 */
@Data
public class Attr implements Serializable {
    private Long attrId;
    private String attrName;
    private String attrValue;
}
