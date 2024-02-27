package com.hxmall.shop.product.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.product.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}

