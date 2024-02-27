package com.hxmall.shop.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/19 11:23
 * @email 815835618@qq.com
 */
public class ProductConstant {

    @Getter
    public enum AttrEnum {
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;

        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
    }

    @Getter
    public enum  productStatus{
        LISTING(1,"商品上架"),
        DELIST(0,"商品下架"),
        UP_FAILL(2,"商品上架失败"),
        UP_SUCCESS(3,"商品上架成功");
        private int code;
        private String msg;

        productStatus(int code,String msg){
            this.code=code;
            this.msg=msg;
        }
    }
}
