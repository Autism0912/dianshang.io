package com.hxmall.shop.ware.ware.vo;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * author:黄龙强
 * time:{2023/12/22}
 * version:1.0
 */
@Data
public class PurchaseDoneVo {
    /**
     * 采购单id
     */
    @NonNull
    private Long id;

    /**
     * 采购需求
     */
    private List<PurchaseItemDoneVo> items;
}
