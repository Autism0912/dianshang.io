package com.hxmall.shop.product.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.product.product.entity.AttrEntity;
import com.hxmall.shop.product.product.vo.AttrRespVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBaseAttr(Map<String, Object> params, Long categroyId, String attrType);

    AttrRespVO getAttrInfo(Long attrId);

    PageUtils getAttrNoRelation(Map<String,Object> params,Long attrGroupId);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    List<Long> searchAttrIds(List<Long> arrIds);
}

