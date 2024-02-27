package com.hxmall.shop.product.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;
import com.hxmall.shop.product.product.dao.BrandDao;
import com.hxmall.shop.product.product.entity.BrandEntity;
import com.hxmall.shop.product.product.service.BrandService;
import com.hxmall.shop.product.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    private final CategoryBrandRelationService categoryBrandRelationService;

    public BrandServiceImpl(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //根据品牌名查询并实现模糊匹配
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        String key =(String) params.get("key");
        if (key != null && !"".equals(key)){
            queryWrapper.and((wrapper)->{
                wrapper.like("name",key);
            });
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
//                new QueryWrapper<BrandEntity>()
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            //同步更新关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
            //TODO 更新其他关联
        }
    }

}