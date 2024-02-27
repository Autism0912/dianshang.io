package com.hxmall.shop.product.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.constant.ProductConstant;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;
import com.hxmall.shop.product.product.dao.AttrAttrgroupRelationDao;
import com.hxmall.shop.product.product.dao.AttrDao;
import com.hxmall.shop.product.product.dao.AttrGroupDao;
import com.hxmall.shop.product.product.dao.CategoryDao;
import com.hxmall.shop.product.product.entity.AttrAttrgroupRelationEntity;
import com.hxmall.shop.product.product.entity.AttrEntity;
import com.hxmall.shop.product.product.entity.AttrGroupEntity;
import com.hxmall.shop.product.product.entity.CategoryEntity;
import com.hxmall.shop.product.product.service.AttrService;
import com.hxmall.shop.product.product.service.CategoryService;
import com.hxmall.shop.product.product.vo.AttrRespVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    private final AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    private final AttrGroupDao attrGroupDao;
    private final CategoryDao categoryDao;
    private final CategoryService categoryService;



    public AttrServiceImpl(AttrAttrgroupRelationDao attrAttrgroupRelationDao, AttrGroupDao attrGroupDao, CategoryDao categoryDao, CategoryService categoryService, AttrDao attrDao) {
        this.attrAttrgroupRelationDao = attrAttrgroupRelationDao;
        this.attrGroupDao = attrGroupDao;
        this.categoryDao = categoryDao;
        this.categoryService = categoryService;

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long categroyId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type",
                "base".equalsIgnoreCase(attrType)
                        ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                        : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()
        );

        String key = (String) params.get("key");
        if (categroyId != 0) {
            queryWrapper.eq("catelog_id", categroyId);
        }

        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page
                = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);


        //结合相关的属性，对 数据中的分类信息和分组信息进行封装
        List<AttrEntity> records = page.getRecords();
        PageUtils pageUtils = new PageUtils(page);
        List<AttrRespVO> attridList = records.stream().map(attrEntity -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);

            //1.设置分类和分组的名称
            if ("base".equalsIgnoreCase(attrType)) {//如果是基本属性，则设置分组名称
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                        attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    //设置属性分组的名称
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //2.查询分类信息
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }
            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(attridList);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVO);

        //基本类型才可以修改
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                    attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity !=null){
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null){
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

        }

        //2.设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] cateLogPath = categoryService.findCatelogPath(catelogId);
        attrRespVO.setCatelogPath(cateLogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null){
            attrRespVO.setCatelogName(categoryEntity.getName());
        }
        return attrRespVO;
    }

    @Override
    public PageUtils getAttrNoRelation(Map<String,Object> params,Long attrGroupId) {
        //1，当前分组只能关联自己所属分类里的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2，当前分组只能关联别的分组没有引用的属性
        //2.1) 当前分类下的其他分组
        List<AttrGroupEntity> groups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id"
                , catelogId));
        List<Long> groupIds = groups.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2）查询这些分组的关联属性
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities =
                attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        List<Long> attrIds = attrgroupRelationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //2.3）从当前分类的所有属性中将这些属性移除掉，剩下就是没有被当前分类没有被关联过的属性[只有基础属性可以被关联]
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds!= null && attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(w-> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        //将结果进行封装
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    /**
     * 根据分组id 查询关联属性
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> entities =
                attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        List<Long> attrIds = entities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

        if (attrIds == null || attrIds.size() == 0) {
            return null;
        }
        return this.listByIds(attrIds);
    }

    @Override
    public List<Long> searchAttrIds(List<Long> arrIds) {
        return baseMapper.searchAttrIds(arrIds);
    }

}