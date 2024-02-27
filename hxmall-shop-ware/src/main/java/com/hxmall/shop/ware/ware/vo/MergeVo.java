package com.hxmall.shop.ware.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
