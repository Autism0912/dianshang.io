package com.hxmall.shop.product.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * author:黄龙强
 * time:{2023/12/29}
 * version:1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog3Vo implements Serializable {
    private String id;
    private String name;
    private String catalog2Id;
}
