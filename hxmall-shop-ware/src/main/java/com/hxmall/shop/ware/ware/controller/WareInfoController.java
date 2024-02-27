package com.hxmall.shop.ware.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hxmall.shop.common.to.SkuInfoTO;
import com.hxmall.shop.common.to.es.SkuHasStockVo;
import com.hxmall.shop.ware.ware.openfeign.ProductSelect;
import com.hxmall.shop.ware.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hxmall.shop.ware.ware.entity.WareInfoEntity;
import com.hxmall.shop.ware.ware.service.WareInfoService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;



/**
 * 仓库信息
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:12:40
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 查询是否有库存
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
       List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
       return R.ok().setData(vos);
    }

}
