package com.hxmall.shop.product.product.service.impl;

import com.hxmall.shop.product.product.vo.AttrGroupRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.product.product.dao.AttrAttrgroupRelationDao;
import com.hxmall.shop.product.product.entity.AttrAttrgroupRelationEntity;
import com.hxmall.shop.product.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    private final AttrAttrgroupRelationDao relationDao;

    public AttrAttrgroupRelationServiceImpl(AttrAttrgroupRelationDao relationDao) {
        this.relationDao = relationDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVO> vos) {
        //对批量保存的数据进行处理
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = vos.stream().map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            attrAttrgroupRelationEntity.setAttrSort(0);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        //批量保存
        this.saveBatch(attrAttrgroupRelationEntities);
    }

    /**
     * 1.删除属性与分组的关联关系
     * 2.
     *
     * @param deletes
     */
    @Override
    public void removeBatchByAidGid(AttrGroupRelationVO[] deletes) {
        //对批量删除的数据进行处理
        List<AttrGroupRelationVO> list = Arrays.asList(deletes);
        List<AttrAttrgroupRelationEntity> entities = list.stream().map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        //执行删除
        relationDao.deleteBatchRelation(entities);

    }

}