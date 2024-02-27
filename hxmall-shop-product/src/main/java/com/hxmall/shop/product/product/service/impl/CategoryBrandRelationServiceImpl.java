package com.hxmall.shop.product.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;
import com.hxmall.shop.product.product.dao.BrandDao;
import com.hxmall.shop.product.product.dao.CategoryBrandRelationDao;
import com.hxmall.shop.product.product.dao.CategoryDao;
import com.hxmall.shop.product.product.entity.BrandEntity;
import com.hxmall.shop.product.product.entity.CategoryBrandRelationEntity;
import com.hxmall.shop.product.product.entity.CategoryEntity;
import com.hxmall.shop.product.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    private final BrandDao brandDao;
    private final CategoryBrandRelationDao categoryBrandRelationDao;
    private final CategoryDao categoryDao;

    public CategoryBrandRelationServiceImpl(BrandDao brandDao, CategoryBrandRelationDao categoryBrandRelationDao,
                                            CategoryDao categoryDao) {
        this.brandDao = brandDao;
        this.categoryBrandRelationDao = categoryBrandRelationDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBrandCategoryRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        //获取到品牌id,三级分类的id
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        //依据id取查询名称
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        //将品牌id为brandId的值更新
        this.update(categoryBrandRelationEntity, new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(catId);
        categoryBrandRelationEntity.setCatelogName(name);
        this.update(categoryBrandRelationEntity, new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> catelogIds =
                categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>()
                        .eq("catelog_id", catId));
        //依据品牌id查询品牌信息
        List<BrandEntity> collect = catelogIds.stream().map(item -> {
            long brandId = item.getBrandId();
            BrandEntity brandEntity = brandDao.selectById(brandId);
            return brandEntity;
        }).collect(Collectors.toList());
        return collect;
    }

}