package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:15
 * @email 815835618@qq.com
 */
@Data
public class Images implements Serializable {
    private String imgUrl;
    private int defaultImg;
}
