package com.hxmall.shop.product.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.product.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> images);
}

