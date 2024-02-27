package com.hxmall.shop.ware.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.to.es.SkuHasStockVo;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.ware.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:12:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    

    PageUtils queryPage(Map<String, Object> params);

    double addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
}

