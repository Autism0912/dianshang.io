package com.hxmall.shop.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:黄龙强
 * time:{2023/12/26}
 * version:1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer id;
    private String gender;
    private String name;
}
