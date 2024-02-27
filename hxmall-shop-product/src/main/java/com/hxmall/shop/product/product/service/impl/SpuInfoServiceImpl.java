package com.hxmall.shop.product.product.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.hxmall.shop.common.constant.ProductConstant;
import com.hxmall.shop.common.to.SkuEsModel;
import com.hxmall.shop.common.to.SkuReductionTo;
import com.hxmall.shop.common.to.SpuBoundTO;
import com.hxmall.shop.common.to.es.SkuHasStockVo;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.product.product.entity.*;
import com.hxmall.shop.product.product.feign.CouponFeignService;
import com.hxmall.shop.product.product.feign.SearchFeignService;
import com.hxmall.shop.product.product.feign.WareFeignService;
import com.hxmall.shop.product.product.service.*;
import com.hxmall.shop.product.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.product.product.dao.SpuInfoDao;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    private final SpuInfoDescService spuInfoDescService;

    private final SpuImagesService spuImagesService;

    private final AttrService attrService;

    private final CouponFeignService couponFeignService;

    private final SkuInfoService skuInfoService;

    private final SkuImagesService skuImagesService;

    private final ProductAttrValueService attrValueService;

    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final WareFeignService wareFeignService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final SearchFeignService searchFeignService;

    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService,
                              SpuImagesService spuImagesService,
                              AttrService attrService,
                              CouponFeignService couponFeignService, SkuInfoService skuInfoService,
                              SkuImagesService skuImagesService,
                              ProductAttrValueService attrValueService,
                              SkuSaleAttrValueService skuSaleAttrValueService, WareFeignService wareFeignService, BrandService brandService, CategoryService categoryService, SearchFeignService searchFeignService) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.couponFeignService = couponFeignService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.attrValueService = attrValueService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.wareFeignService = wareFeignService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.searchFeignService = searchFeignService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.保存spu的基本信息 pms_sku_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        BeanUtils.copyProperties(vo, spuInfoEntity);

        //插入以后id会自动注入到spuInfoEntity中
        this.saveBatchSpuInfo(spuInfoEntity);
        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        //3.保存spu的图集信息 pms_spu_images
        List<String> images = vo.getImages();
        //保存的时候我们组要指定我们保存时哪一个spu的图片
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        //4.保存spu的规格参数信息 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            //页面可能没有传入属性名称，需要从数据库中查询
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());

            return productAttrValueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttrValue(productAttrValueEntities);
        //5.保存spu的对应的sku信息(远程调用)
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        //发起远程调用
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }
        //5.1) sku的积分信息 sms_spu_bounds （远程调用）
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            //处理默认图片
            for (Skus sku : skus) {
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }

                // //5.2) sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                //设置spu的品牌信息
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount((long) (Math.random() * 2888));
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                //5.3) sku的图片信息 pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imageEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());

                    return skuImagesEntity;
                }).filter(entity -> {
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(imageEntities);

                //5.4) sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.5) sku的优惠、满减等信息 (远程调用)
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);

                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            }
        }
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
//        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
//        //依据spu管理发送的参数进行模糊查询
//        String key = (String) params.get("key");
//        if (!StringUtils.isEmpty(key)) {
//            queryWrapper.and((wrapper) -> {
//                wrapper.eq("id", key).or().like("spu_name", key);
//            });
//        }
//
//        String status = (String) params.get("status");
//        if (!StringUtils.isEmpty(status)) {
//            queryWrapper.eq("publish_status", status);
//        }
//        String brandId = (String) params.get("brandId");
//        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
//            queryWrapper.eq("brand_id", brandId);
//        }
//
//        String catelogId = (String) params.get("catelogId");
//        queryWrapper.eq("catalog_id", catelogId);
//
//        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);
//        return new PageUtils(page);
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        //依据spu管理发送的参数进行模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params),
                queryWrapper);

        return new PageUtils(page);
    }

    /**
     * 商品上架
     *
     * @param spuId
     */
    @Override
    public void productUp(Long spuId) {
        //1.根据spuId查询对应的sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
        //2.查询这些sku是不是有库存
        List<Long> skuIds = skus.stream().map(sku -> sku.getSkuId()).collect(Collectors.toList());
        //3.封装每个sku的信息
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrListForSpu(spuId);
        //4.获取到基础的id
        List<Long> arrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        //5.过滤出来可以被检索的属性id,search_type =1[4\5\6等不能被检索]
        Set<Long> ids = new HashSet<>(attrService.searchAttrIds(arrIds));
        //将能够被检索的属性进行封装到SkuService.attr中
        List<SkuEsModel.Attrs> attrs = baseAttrs.stream().filter(attr -> {
            return ids.contains(attr.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        //每个sku是否有库存
        Map<Long, Boolean> stockMap = null;

        //getSkuHasStock
        //3,远程调用库存系统,查询该sku是否有库存
        try {
            R hasStock = wareFeignService.getSkuHasStock(skuIds);
            //在次创建一个匿名对象
           stockMap =  hasStock.getData(new TypeReference<List<SkuHasStockVo>>() {
            }).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            log.warn("远程调用成功");
        } catch (Exception e) {
            log.error("库存服务异常,原因{}", e);
        }

        //将sku 信息保存到es中
        Map<Long,Boolean> finalStockMap = stockMap;

        List<SkuEsModel> skuEsModels = skus.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            skuEsModel.setSaleCount(1000L);
            //设置库存,只查询有库存的
            if (finalStockMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //TODO: 刚上架热度为0
            skuEsModel.setHotScore(0L);
            //设置品牌信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());

            //设置分类信息
            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());

            //保存商品属性信息
            skuEsModel.setAttrs(attrs);
            return skuEsModel;
        }).collect(Collectors.toList());
        //发给es进行保存

        R r = searchFeignService.productStatusUp(skuEsModels);
        int code = ProductConstant.productStatus.LISTING.getCode();

        if (r.getCode()==0){
            log.info("拿到修改code{}",code);
            baseMapper.updateSpuStatus(spuId,ProductConstant.productStatus.LISTING.getCode());
        }

    }

    private void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}