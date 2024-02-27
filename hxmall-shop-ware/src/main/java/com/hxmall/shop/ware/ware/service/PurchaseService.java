package com.hxmall.shop.ware.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.ware.ware.entity.PurchaseEntity;
import com.hxmall.shop.ware.ware.vo.MergeVo;
import com.hxmall.shop.ware.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:12:40
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);


    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);



//    boolean complete(Long id);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}

