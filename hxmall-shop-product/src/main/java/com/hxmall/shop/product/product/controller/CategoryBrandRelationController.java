package com.hxmall.shop.product.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.product.product.entity.BrandEntity;
import com.hxmall.shop.product.product.entity.CategoryBrandRelationEntity;
import com.hxmall.shop.product.product.service.CategoryBrandRelationService;
import com.hxmall.shop.product.product.vo.BrandVo;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {

    private final CategoryBrandRelationService categoryBrandRelationService;

    public CategoryBrandRelationController(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

//    @GetMapping("/brands/list")
//    public R categoryBrandRelation(@RequestBody CategoryBrandRelationEntity[] categoryBrandRelationEntities) {
//        categoryBrandRelationService.saveBatch(Arrays.asList(categoryBrandRelationEntities));
//        return R.ok();
//    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 获取当前品牌关联的所有分类
     */
    @GetMapping("/catelog/list")
    public R categoryList(@RequestParam("brandId") Long bandId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", bandId));
        return R.ok().put("data", categoryBrandRelationEntityList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveBrandCategoryRelation(categoryBrandRelation);

        return R.ok();
    }

    @GetMapping("/brands/list")
    public R categoryBrandRelation(@RequestParam("catId") Long catId) {
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVo> collect = brandEntities.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", collect);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
