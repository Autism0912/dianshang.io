package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * author:黄龙强
 * time:{2023/12/19}
 * version:1.0
 */
@Data
public class AttrRespVO extends AttrVO implements Serializable {
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
