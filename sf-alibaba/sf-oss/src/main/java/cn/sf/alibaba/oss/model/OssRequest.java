package cn.sf.alibaba.oss.model;

import cn.sf.alibaba.oss.enums.BizOssEnum;
import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.io.Serializable;

@Data
@ToString
public class OssRequest implements Serializable {

    private String fileName;
    //最终在OSS上存储的路径将会是: bizCodePath/file-uuid.pdf
    private BizOssEnum bizEnum;
    private String fileType;
    private String description;
    private InputStream inputStream;
    //是否附件:指定该属性值,将会告诉浏览器以附件形式下载,而不需要在线预览
    private boolean isAttachment = false;


}