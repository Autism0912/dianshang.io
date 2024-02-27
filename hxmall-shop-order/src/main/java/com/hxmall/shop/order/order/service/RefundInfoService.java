package com.hxmall.shop.order.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.order.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 11:42:27
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

