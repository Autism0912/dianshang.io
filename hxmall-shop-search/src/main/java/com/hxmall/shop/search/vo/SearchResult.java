package com.hxmall.shop.search.vo;

import com.hxmall.shop.common.to.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Description 检索的结果集
 * @Author ghl
 * @Date 2024/1/3 11:00
 * @email 815835618@qq.com
 */
@Data
public class SearchResult {
    /**
     * 查询到的所有的商品结果
     */
    private List<SkuEsModel> products;
    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页码
     */
    private Integer totalPages;
    /**
     * 查询结果中包含到的所有的品牌信息
     */
    private List<BrandVo> brands;
    /**
     * 查询结果中包含到的所有的分类信息
     */
    private List<CatalogVo> catalogs;

    /**
     * 查询结果中所有涉及到属性
     */
    private List<AttrVo> attrs;

    /**
     * 导航页
     */
    private List<Integer> pageNavs;

    /**
     * 导航数据
     */
    private List<NavVo> navs = new ArrayList<>();

    /**
     * 判断当前id是否是被使用的
     */
    private List<Long> attrsIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}

