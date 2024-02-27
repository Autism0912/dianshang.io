package com.hxmall.shop.product.product.dao;

import com.hxmall.shop.product.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {


    List<Long> searchAttrIds(@Param("attrIds") List<Long> arrIds);
}
