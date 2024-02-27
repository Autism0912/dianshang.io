package com.hxmall.shop.ware.ware.service.impl;

import com.hxmall.shop.common.to.SkuInfoEntity;
import com.hxmall.shop.common.to.es.SkuHasStockVo;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.ware.ware.openfeign.ProductSelect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.ware.ware.dao.WareSkuDao;
import com.hxmall.shop.ware.ware.entity.WareSkuEntity;
import com.hxmall.shop.ware.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    private final WareSkuDao wareSkuDao;

    private final ProductSelect productSelect;

    public WareSkuServiceImpl(WareSkuDao wareSkuDao, ProductSelect productSelect) {
        this.wareSkuDao = wareSkuDao;
        this.productSelect = productSelect;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        //获取仓库id
        String wareId = (String) params.get("wareId");
        //获取商品Id
        String skuId = (String) params.get("skuId");

        if (wareId != null && !"".equals(wareId)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("ware_id",wareId);
            });
        }
        if (skuId!=null && !"".equals(skuId)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",skuId);
            });
        }


//        if (!StringUtils.isEmpty(key)){
//            queryWrapper.and(wrapper -> {
//                queryWrapper.eq("id",key).or().like("address",key).or().like("areacode",key);
//            });
//        }
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public double addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.如果没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        double price = 0.0;
        //TODO:之后实现出现异常不用回滚
        WareSkuEntity wareSkuEntity = new WareSkuEntity();
        //远程调用
        try{
            R info = productSelect.info(skuId);
            Map<String,Object> data = (Map<String,Object>) info.get("skuInfo");
            if (info.getCode() == 0 ){
                wareSkuEntity.setSkuName((String) data.get("skuName"));
                //设置商品价格
                price = (double) data.get("price");
            }
        }catch (Exception e){
            log.error("商品服务远程嗲用失败,WareSkuServiceImpl",e.getMessage());
        }

        //新增
        if (wareSkuEntities == null || wareSkuEntities.size() == 0){
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            wareSkuDao.insert(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
        return price;
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId->{
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询当前sku的总库存量
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(baseMapper.getSkuStock(skuId)==null?false:true);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

}