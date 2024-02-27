package com.hxmall.shop.coupon.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.coupon.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:02:36
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

