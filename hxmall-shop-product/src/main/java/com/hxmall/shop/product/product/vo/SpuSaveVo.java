package com.hxmall.shop.product.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/20 15:04
 * @email 815835618@qq.com
 */
@Data
public class SpuSaveVo implements java.io.Serializable{
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    /**
     * 描述图片信息
     */
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;

}
