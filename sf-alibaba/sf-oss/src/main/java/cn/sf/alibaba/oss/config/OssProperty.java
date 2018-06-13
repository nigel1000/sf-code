package cn.sf.alibaba.oss.config;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString(exclude = {"key","secret"})
@Data
public class OssProperty implements Serializable {

     // Config Access account regoin.
    private String region = "cn-hangzhou";
     // Config Access account SDK version.
    private String version = "2015-04-01";
    // for RAM user
    private String roleRam;
    // token live time
    private Long time = 900L;
    //影响获取的下载链接参数
    private boolean isPrivate;
    // access secret.
    // access key.
    private String key;
    private String secret;
    private String endPoint = "http://oss-cn-hangzhou.aliyuncs.com" ;
    private String bucket;

}