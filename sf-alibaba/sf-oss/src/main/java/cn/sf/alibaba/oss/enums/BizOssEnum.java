package cn.sf.alibaba.oss.enums;

import java.util.HashMap;
import java.util.Map;

//提供给外部用
//指定容器内路径地址
public enum BizOssEnum {

    NULL(-1,"null","null","null"),

    CAR_MEDIA(1014,"车媒体","1014AN","pub-doc"),

    ;

    BizOssEnum(int bizCode, String bizDesc, String filePath, String bucketAccess){
        this.bizCode = bizCode;
        this.bizDesc = bizDesc;
        this.filePath = filePath;
        this.bucketAccess = bucketAccess;
    }

    private final int bizCode;
    private final String bizDesc;
    //指定放到篮子的目录
    private final String filePath;
    private final String bucketAccess;


    private static Map<Integer, BizOssEnum> map = new HashMap<>();
    static {
        for (BizOssEnum item : BizOssEnum.values()) {
            map.put(item.getBizCode(), item);
        }
    }
    public BizOssEnum genEnumByBizCode(int bizCode) {
        return map.get(bizCode);
    }

    public int getBizCode() {
        return bizCode;
    }

    public String getBizDesc() {
        return bizDesc;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getBucketAccess() {
        return bucketAccess;
    }
}