package com.hxmall.shop.ware.ware.vo;

import lombok.Data;

/**
 * author:黄龙强
 * time:{2023/12/22}
 * version:1.0
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Long status;
    private String reason;
}
