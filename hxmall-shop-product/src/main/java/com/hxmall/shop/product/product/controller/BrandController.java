package com.hxmall.shop.product.product.controller;

import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.common.valid.AddGroup;
import com.hxmall.shop.common.valid.UpdateGroup;
import com.hxmall.shop.product.product.entity.BrandEntity;
import com.hxmall.shop.product.product.service.BrandService;
import com.hxmall.shop.product.product.service.CategoryBrandRelationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Map;



/**
 * 品牌
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {

    private final BrandService brandService;


    public BrandController(BrandService brandService, CategoryBrandRelationService categoryBrandRelationService) {
        this.brandService = brandService;

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
