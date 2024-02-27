package com.hxmall.shop.product.product.controller;

import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.product.product.entity.AttrEntity;
import com.hxmall.shop.product.product.entity.AttrGroupEntity;
import com.hxmall.shop.product.product.service.AttrAttrgroupRelationService;
import com.hxmall.shop.product.product.service.AttrGroupService;
import com.hxmall.shop.product.product.service.AttrService;
import com.hxmall.shop.product.product.service.CategoryService;

import com.hxmall.shop.product.product.vo.AttrGroupRelationVO;
import com.hxmall.shop.product.product.vo.AttrGroupWithAttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    private final CategoryService categoryService;
    private final AttrService attrService;
    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    public AttrGroupController(CategoryService categoryService, AttrService attrService, AttrAttrgroupRelationService attrAttrgroupRelationService) {
        this.categoryService = categoryService;
        this.attrService = attrService;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrWithAttrGroup(@PathVariable("catelogId") Long catelogId){
        //1.查询当前分类下所有属性分组
       List<AttrGroupWithAttrVO> attrGroupEntities =  attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
       //2.查询每个分组信息
        return R.ok().put("data",attrGroupEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catalogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catalogId") Long catalogId){
        PageUtils page = attrGroupService.queryPage(params,catalogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        //用当前分类的id查询完整的路径
        attrGroup.setCatelogPath(categoryService.findCatelogPath(attrGroup.getCatelogId()));
        return R.ok().put("attrGroup", attrGroup);
    }

    @PostMapping("attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVO> vos){
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }


    @PostMapping("/attr/relation/delete")
    public R delete(@RequestBody AttrGroupRelationVO[] deletes){
        attrAttrgroupRelationService.removeBatchByAidGid(deletes);
        return R.ok();
    }
    /**
     * 获取没有关联过的属性
     * @param params
     * @param attrGroupId
     * @return
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String,Object> params,@PathVariable("attrGroupId") Long attrGroupId){
        PageUtils attrs =  attrService.getAttrNoRelation(params,attrGroupId);
       return R.ok().put("data",attrs);
    }

    /**
     *
     * @param attrGroupId
     * @return
     */

    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId")Long attrGroupId){
        List<AttrEntity> attrs =  attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data",attrs);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
