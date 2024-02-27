package com.hxmall.shop.ware.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.ware.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:12:40
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean setObtian(Long purchaseId);


    boolean updatePrice(PurchaseDetailEntity purchaseDetailEntity1);
}

