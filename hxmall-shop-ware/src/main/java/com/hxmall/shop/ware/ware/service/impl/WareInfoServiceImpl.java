package com.hxmall.shop.ware.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;

import com.hxmall.shop.ware.ware.dao.WareInfoDao;
import com.hxmall.shop.ware.ware.entity.WareInfoEntity;
import com.hxmall.shop.ware.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        //获取key
        String key = (String) params.get("key");
        if (key !=null && !"".equals(key)){
            queryWrapper.and((wrapper)->{
                wrapper.like("name",key).or().like("id",key)
                        .or().like("address",key)
                        .or().like("areacode",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper

        );

        return new PageUtils(page);
    }

}