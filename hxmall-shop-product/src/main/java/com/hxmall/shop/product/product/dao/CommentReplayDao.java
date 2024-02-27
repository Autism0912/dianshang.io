package com.hxmall.shop.product.product.dao;

import com.hxmall.shop.product.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
