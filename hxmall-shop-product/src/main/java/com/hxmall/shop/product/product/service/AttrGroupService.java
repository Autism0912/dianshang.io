package com.hxmall.shop.product.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.product.product.entity.AttrGroupEntity;
import com.hxmall.shop.product.product.vo.AttrGroupWithAttrVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author gavin
 * @email 815835618@qq.com
 * @date 2023-12-11 16:57:33
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params, Long catalogId);

    List<AttrGroupWithAttrVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

