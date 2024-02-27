package com.hxmall.shop.member.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.member.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:07:23
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

