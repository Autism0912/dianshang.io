package com.hxmall.shop.product.product.vo;

import com.hxmall.shop.product.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/20}
 * version:1.0
 */
@Data
public class AttrGroupWithAttrVO implements Serializable {
    private String attrGroupName;
    private Integer sort;
    private String descript;
    private String icon;
    private Long catelogId;
    private List<AttrEntity> attrs;
}
