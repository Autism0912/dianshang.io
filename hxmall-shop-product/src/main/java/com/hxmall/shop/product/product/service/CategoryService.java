package com.hxmall.shop.product.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.product.product.entity.CategoryEntity;
import com.hxmall.shop.product.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    Long[] findCatelogPath(Long catelogId);

    void updateCategoryBrandRelation(CategoryEntity category);

    List<CategoryEntity> getLevelCategorys();

    Map<String, List<Catelog2Vo>> getCatalogJson();
    Map<String, List<Catelog2Vo>> getCatalogJsonFromResdis();
    Map<String, List<Catelog2Vo>> getCatalogJsonFromDB();
}

