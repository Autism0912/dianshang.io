package com.hxmall.shop.ware.ware.service.impl;

import com.hxmall.shop.common.constant.WareConstant;
import com.hxmall.shop.ware.ware.service.PurchaseService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.ware.ware.dao.PurchaseDetailDao;
import com.hxmall.shop.ware.ware.entity.PurchaseDetailEntity;
import com.hxmall.shop.ware.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("purchase_id", key).or().eq("sku_id", key);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public boolean setObtian(Long purchaseId) {
        //1.先根据purchaseId
        PurchaseDetailEntity checklist  = this.getOne(new QueryWrapper<PurchaseDetailEntity>()
                .eq("id", purchaseId));
        //2.判断清单是否为空，为空领取失败，不为空修改该清单状态为已经领取
        if (StringUtils.isEmpty(checklist)){return false;}

        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
        purchaseDetailEntity.setPurchaseId(purchaseId);
        //设置状态
        purchaseDetailEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
        if (!this.updateById(purchaseDetailEntity)){return false;}
        return true;
    }

    @Override
    public boolean updatePrice(PurchaseDetailEntity purchaseDetailEntity1) {
        if (this.updateById(purchaseDetailEntity1)){
            return true;
        }
        return false;
    }


}