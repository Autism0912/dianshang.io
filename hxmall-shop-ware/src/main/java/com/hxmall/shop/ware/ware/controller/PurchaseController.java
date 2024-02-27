package com.hxmall.shop.ware.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hxmall.shop.ware.ware.vo.MergeVo;
import com.hxmall.shop.ware.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hxmall.shop.ware.ware.entity.PurchaseEntity;
import com.hxmall.shop.ware.ware.service.PurchaseService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;



/**
 * 采购信息
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:12:40
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;



    /**
     * 领取采购单 Mr.guo
     * 某个采购员领取了采购单以后,先看一下当前采购单是不是处于未分配状态,只有采购处于新建或者已领取的状态时候才能更新采购单状态
     */
    @PostMapping("/reveive")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
    return R.ok();
    }

    @PostMapping("/done")
    public R purchasesFinsh(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.done(purchaseDoneVo);
        return R.ok();
    }




    @GetMapping("/unreceive/list")
     public R getPurchaseUnreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(params);
        return R.ok().put("page",page);
    }

    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeVo mergeVo){
            purchaseService.mergePurchase(mergeVo);
            return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
