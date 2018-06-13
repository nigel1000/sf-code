package cn.sf.alibaba.oss.test;

import cn.sf.alibaba.oss.base.BaseTest;
import cn.sf.alibaba.oss.client.OssObjectUtil;
import cn.sf.alibaba.oss.client.OssUrlUtil;
import cn.sf.alibaba.oss.enums.BizOssEnum;
import cn.sf.alibaba.oss.model.OssRequest;
import cn.sf.bean.beans.Response;
import com.aliyun.oss.model.OSSObject;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by nijianfeng on 17/6/25.
 */
public class TestOssUtil extends BaseTest {

    @Resource
    private OssUrlUtil ossUrlUtil;
    @Resource
    private OssObjectUtil ossObjectUtil;

    private final static String fileId = "1014AN/5c84fdb9-8590-45c3-8c5f-10def57a49e9.jpg";
    private final static String path = TestOssUtil.class.getResource("/").getPath();

    @Test
    public void testGetUrl(){
        System.out.println(ossUrlUtil.getOssDownloadUrl(1014,fileId));
    }
    @Test
    public void testDelObject(){
        boolean ret = ossObjectUtil.deleteObject(
                BizOssEnum.CAR_MEDIA,
                fileId);
        System.out.println(ret);
    }
    @Test
    public void testPutObject(){
        OssRequest request = new OssRequest();
        request.setDescription("测试用");
        // 传业务类型可以对应到篮子类型和指定路径
        // 篮子类型可以确定用哪个oss client(也拿到了配置文件中的bucket)
        request.setBizEnum(BizOssEnum.CAR_MEDIA);
        request.setFileName("第一个文件.jpg");
        request.setFileType("image/jpg");
        try {
            request.setInputStream(new FileInputStream(path+"oss_test.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(request.getInputStream().available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Response<String> path = ossObjectUtil.putObject(request);
        System.out.println(path);
//        1099OT/28456a41-a3ef-4442-be8e-885f4b06fbc9.jpg,048D40EB852DF733277C5B6BC1C5857B
//        1099OT/21f4cdfb-94e8-4da5-a8e4-4d3552596faf.jpg
//        1099OT/bba15a9c-c1b3-4533-90aa-db14ad194f7c.jpg
//        1014AN/8ddaff76-7929-493a-bb3c-88880b68c695.jpg
//        1014AN/419eb5c7-d956-4a81-8850-d186f721a0d4.jpg
    }
    @Test
    public void testGetObject(){
        Response<OSSObject> objectResponse = ossObjectUtil.getObject(
                BizOssEnum.CAR_MEDIA,
                fileId);
        System.out.println(objectResponse);
        if(objectResponse.isSuccess()) {
            try {
                System.out.println(objectResponse.getResult().getObjectContent().available());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
