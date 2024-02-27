package com.hxmall.shop.product.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/29}
 * version:1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo implements Serializable {
    private String id;
    private String name;
    private String catalog1Id;
    private List<Catalog3Vo> catalog3List;
}
