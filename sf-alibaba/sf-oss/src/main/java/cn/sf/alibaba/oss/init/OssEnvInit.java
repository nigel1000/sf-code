package cn.sf.alibaba.oss.init;

import cn.sf.alibaba.oss.config.OssProperties;
import cn.sf.alibaba.oss.config.OssProperty;
import cn.sf.alibaba.oss.enums.BizOssEnum;
import cn.sf.bean.constants.LogString;
import com.aliyun.oss.OSSClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 17/6/26.
 */
@Component
@Slf4j
public class OssEnvInit implements InitializingBean {

    //oss 客户端类
    protected static Map<String, OssProperty> accessDataMap = Maps.newHashMap();
    protected static Map<String, OSSClient> clientsMap = Maps.newHashMap();
    //初始化ossClient和accessData
    @Resource
    private OssProperties ossProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        //只初始化一次  防止生成子类时再次调用
        if(OssEnvInit.class==this.getClass()) {
            log.info(LogString.initPre + "OssEnvInit init accessDataMap&clientsMap:"+ ossProperties.toString());
            accessDataMap = ossProperties.getAccessDataMap();
            List<String> nonProperties = Lists.newArrayList();
            accessDataMap.forEach((accessKey, accessData) -> {
                OssEnvInit.BucketEnum bucketEnum = OssEnvInit.BucketEnum.NULL.genEnumByAccess(accessKey);
                if (bucketEnum != null) {
                    OSSClient client = new OSSClient(accessData.getEndPoint(), accessData.getKey(), accessData.getSecret());
                    clientsMap.put(accessKey, client);
                } else {
                    nonProperties.add(accessKey);
                    log.warn(LogString.initPre + "BucketEnum中没有" + accessKey + "对应的编码!");
                }
            });
            //删除BucketEnum中没有的access
            nonProperties.forEach((key)->{
                accessDataMap.remove(key);
            });
            log.info(LogString.initPre + "OssProperties init: " + ossProperties.toString());

            // 关闭 oss 客户端
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                clientsMap.forEach((key, client) -> {
                    try {
                        client.shutdown();
                        log.info(LogString.initPre + "关闭OSSClient key:{} ---> 成功!", key);
                    } catch (Exception e) {
                        log.error(LogString.initPre + "关闭OSSClient key:{} 失败!", key);
                        log.error(LogString.initPre + "关闭OSSClient Exception: ", e);
                    }
                });
            }));
        }
    }

    protected enum BucketEnum {

        NULL("默认使用","null"),

        DEV_PUB_DOC_WRITE("开发环境公共写","pub-doc"),

        ;

        private final String desc;
        private final String access;


        private static Map<String, BucketEnum> map = new HashMap<>();
        static {
            for (BucketEnum item : BucketEnum.values()) {
                map.put(item.getAccess(), item);
            }
        }

        // 构造函数
        BucketEnum(String desc, String access) {
            this.access = access;
            this.desc = desc;
        }

        public BucketEnum genEnumByAccess(String access) {
            return map.get(access);
        }

        public String getAccess() {
            return access;
        }
        public String getDesc() {
            return desc;
        }

    }
    
    
    protected BucketEnum checkBizEnum(BizOssEnum bizEnum){
        if(bizEnum==null){
            throw new IllegalArgumentException("checkBizEnum ---> bizEnum is null!!!");
        }
        BucketEnum key = BucketEnum.NULL.genEnumByAccess(bizEnum.getBucketAccess());
        if(key==null){
            throw new IllegalArgumentException("checkBizEnum ---> "+bizEnum.getBucketAccess()+
                    "配置不存在!!!");
        }
        return key;
    }



}
