package cn.sf.alibaba.oss.client;

import cn.sf.alibaba.oss.config.OssProperty;
import cn.sf.alibaba.oss.enums.BizOssEnum;
import cn.sf.alibaba.oss.init.OssEnvInit;
import cn.sf.bean.beans.Response;
import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Date;

/**
 * Created by nijianfeng on 17/6/26.
 */
@Slf4j
public final class OssUrlUtil extends OssEnvInit {

    public Response<String> getOssDownloadUrl(int bizCode, String fileId){

        BizOssEnum bizEnum = BizOssEnum.NULL.genEnumByBizCode(bizCode);
        BucketEnum key = checkBizEnum(bizEnum);
        //1.获取上传AccessKey  配置RAM用户AK信息
        OssProperty accessData = accessDataMap.get(key.getAccess());
        //2.获取OSSClient
        OSSClient ossClient = clientsMap.get(key.getAccess());
        //3.获取url
        URL url = ossClient.generatePresignedUrl(
                accessData.getBucket(),
                fileId,
                new Date(new Date().getTime() + accessData.getTime() * 1000));
        String result = url.toString();
        if(!accessData.isPrivate()){
            result = result.substring(0,result.lastIndexOf("?"));
        }
        return Response.ok(result);
    }

}
