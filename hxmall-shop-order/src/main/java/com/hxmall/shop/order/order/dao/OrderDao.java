package com.hxmall.shop.order.order.dao;

import com.hxmall.shop.order.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 11:42:27
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
