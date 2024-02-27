package com.hxmall.shop.product.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;
import com.hxmall.shop.product.product.dao.AttrGroupDao;
import com.hxmall.shop.product.product.entity.AttrEntity;
import com.hxmall.shop.product.product.entity.AttrGroupEntity;
import com.hxmall.shop.product.product.service.AttrGroupService;
import com.hxmall.shop.product.product.service.AttrService;
import com.hxmall.shop.product.product.vo.AttrGroupWithAttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    private final AttrService attrService;

    public AttrGroupServiceImpl(AttrService attrService) {
        this.attrService = attrService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {
        String key = (String)params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        if (key!= null &&!"".equals(key)) {
            wrapper.and(w -> w.eq("attr_group_id", key).or().like("attr_group_name", key));
        }

        if (catalogId == 0) { //分类id为0查询所有
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catalogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1.查询当前分类下所有分组
        List<AttrGroupEntity> attrGroupEntities =
                this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //2.查询所有属性
        List<AttrGroupWithAttrVO> attrGroupWithAttrVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrVO attrGroupWithAttrVO = new AttrGroupWithAttrVO();
            BeanUtils.copyProperties(attrGroupEntities, attrGroupWithAttrVO);
            //按照分组的id去查询所有的关联属性
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupEntity.getAttrGroupId());
            attrGroupWithAttrVO.setAttrs(relationAttr);
            return attrGroupWithAttrVO;
        }).collect(Collectors.toList());
        return attrGroupWithAttrVos;
    }


}