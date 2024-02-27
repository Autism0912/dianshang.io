package com.hxmall.shop.thirdpart.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.hxmall.shop.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * author:黄龙强
 * time:{2023/12/14}
 * version:1.0
 */
@RestController
@RequestMapping("/thirdparty/oss")
public class OssController {

    //从配置文件中读取我们的信息
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;
    private final OSS ossClient;

    public OssController(OSS ossClient) {
        this.ossClient = ossClient;
    }

    @GetMapping("/policy")
    public R policy(){
        String host = "https://"+bucket+"."+endpoint;//上传地址
        //用户在上传的时候需要指定一个前缀
        String dir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

        //创建ossClient的实例
        Map<String,String> resp = null;

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis()+expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            //请求最大支持文件大小
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE,0,1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith,PolicyConditions.COND_KEY,dir);

            String postPolicy = ossClient.generatePostPolicy(expiration,policyConditions);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodePolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            resp = new HashMap<String,String>();
            resp.put("accessId",accessId);
            resp.put("policy",encodePolicy);
            resp.put("signature",postSignature);
            resp.put("dir",dir);
            resp.put("host",host);
            resp.put("expire",String.valueOf(expireEndTime/1000));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ossClient.shutdown();
        }
        return R.ok().put("data",resp);
    }


}
