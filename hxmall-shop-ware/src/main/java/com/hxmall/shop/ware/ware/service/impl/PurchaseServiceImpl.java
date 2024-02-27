package com.hxmall.shop.ware.ware.service.impl;

import com.hxmall.shop.common.constant.WareConstant;
import com.hxmall.shop.ware.ware.entity.PurchaseDetailEntity;
import com.hxmall.shop.ware.ware.openfeign.ProductSelect;
import com.hxmall.shop.ware.ware.service.PurchaseDetailService;
import com.hxmall.shop.ware.ware.service.WareSkuService;
import com.hxmall.shop.ware.ware.vo.MergeVo;
import com.hxmall.shop.ware.ware.vo.PurchaseDoneVo;
import com.hxmall.shop.ware.ware.vo.PurchaseItemDoneVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.ware.ware.dao.PurchaseDao;
import com.hxmall.shop.ware.ware.entity.PurchaseEntity;
import com.hxmall.shop.ware.ware.service.PurchaseService;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    private final PurchaseDetailService purchaseDetailService;

//    private final ProductSelect productSelect;

    private final WareSkuService wareSkuService;
    public PurchaseServiceImpl(PurchaseDetailService purchaseDetailService, ProductSelect productSelect, WareSkuService wareSkuService) {
        this.purchaseDetailService = purchaseDetailService;
//        this.productSelect = productSelect;
        this.wareSkuService = wareSkuService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
//        String status = (String) params.get("status");
        String status = (String) params.get("status");

        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();

        if (key!=null && !"".equals(key)){
            queryWrapper.and((wrapper)->{
                wrapper.like("assignee_name",key)
                        .or().like("phone",key)
                        .or().eq("id",key)
                        .or().eq("ware_id",key);
            });
        }

        if (status!=null && !"".equals(status)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("status",status);
            });
        }

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params),
                //采购状态只能是0或者1的采购才能被合并
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            //如果没有采购单id 就新建采购单id
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //合并采购单【就是修改上面的采购单】
        List<Long> items = mergeVo.getItems();
        //我们需要从数据库中查询出所有要合并的采购单，状态需要大于【已分配】的状态订单
        PurchaseEntity purchaseEntity = new PurchaseEntity();

        List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.getBaseMapper().selectBatchIds(items).stream()
                .filter(item -> {
                    //如果当前是正在合并的采购项目b需要把之前的采购改为 已分配
                    //如果没有去采购，就可以更改
                    return item.getStatus() < WareConstant.PurchaseDetailStatusEnum.BUYING.getCode()
                            || item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode();
                }).collect(Collectors.toList());
        //将上面的数据过滤掉
        items = purchaseDetailEntityList.stream().map(entity ->
                entity.getId()).collect(Collectors.toList());
        if (items == null || items.size() == 0) {
            //如果没有采购项就不用合并
            return;
        }

        //设置仓库Id //采购单需要是同一个仓库的
        purchaseEntity.setWareId(purchaseDetailEntityList.get(0).getWareId());

        //给采购需求设置采购单和状态信息
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        //更新一下采购时间
        purchaseDetailService.updateBatchById(collect);
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }


    @Override
    public void received(List<Long> ids) {
        //没有采购需求的采购单直接返回,否则会破坏采购单
        if (ids == null || ids.size() == 0){
            return;
        }
        //1.确认当前采购单是出于分配的状态
        List<PurchaseEntity> purchaseEntities = this.listByIds(ids);
        purchaseEntities.stream().filter(item->
            item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode())
                .map(item->{
                    //更新采购单的状态和时间
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                }).collect(Collectors.toList());
        //被领取的采购单重新设置采购状态
        this.updateBatchById(purchaseEntities);

        //3.更新采购需求的状态
        List<Long> purchaseIds = purchaseEntities.stream().map(purchaseEntity ->
                purchaseEntity.getId()).collect(Collectors.toList());
        //通过采购单Id找到所有的采购需求
        QueryWrapper<PurchaseDetailEntity> purchaseDetailIds = new QueryWrapper<PurchaseDetailEntity>()
                .in("purchase_id", purchaseIds);
        //采购单详情
        List<PurchaseDetailEntity> purchaseDetailEntitys = purchaseDetailService.list(purchaseDetailIds);
        //更新采购需求的状态为正在采购
        purchaseDetailEntitys= purchaseDetailEntitys.stream().map(purchaseDetailEntity -> {
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntitys);
    }

    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //1.改变采购单状态
        Long purchaseId = purchaseDoneVo.getId();
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
        double price = 0;
        double p = 0;
        double sum = 0;

        //2.改变采购需求状态
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
            } else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //查询采购单的详细信息
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                //3.将采购成功的商品入库
                price = wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());

                if (price == 0){
                    p = detailEntity.getSkuNum() * price;
                }

                purchaseDetailEntity.setId(item.getItemId());
                updates.add(purchaseDetailEntity);
            }
            purchaseDetailService.updateBatchById(updates);

            //4.改变采购单的状态
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setAmount(new BigDecimal(sum));
            purchaseEntity.setStatus(flag? WareConstant.PurchaseStatusEnum.FINISH.getCode() :  WareConstant.PurchaseStatusEnum.HASERROR.getCode());
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }


}