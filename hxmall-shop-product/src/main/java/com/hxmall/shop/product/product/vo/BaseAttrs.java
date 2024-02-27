package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Description 基础属性信息
 * @Author ghl
 * @Date 2023/12/20 15:09
 * @email 815835618@qq.com
 */
@Data
public class BaseAttrs implements Serializable {
    private Long attrId;
    private String attrValues;
    private int showDesc;
}
