package com.hxmall.shop.member.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.hxmall.shop.member.member.feign.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.hxmall.shop.member.member.entity.MemberEntity;
import com.hxmall.shop.member.member.service.MemberService;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.R;



/**
 * 会员
 *
 * @author longqiang
 * @email 2950366288@qq.com
 * @date 2023-12-12 12:07:23
 */
@RefreshScope //刷新域
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponService couponService;

    @Value("${member.user.name}")
    private String username;

    @Value("${member.user.password}")
    private String password;

    @GetMapping("/test01")
    public R Test02(){
    return R.ok().put("username:",username).put("password:",password);
    }

    @GetMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("会员名称:张三");
        R memberCoupons = couponService.memberCoupons();
        return R.ok().put("member",memberEntity).put("coupons",memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
