package cn.sf.alibaba.oss.client;

import cn.sf.alibaba.oss.config.OssProperty;
import cn.sf.alibaba.oss.enums.BizOssEnum;
import cn.sf.alibaba.oss.init.OssEnvInit;
import cn.sf.alibaba.oss.model.OssRequest;
import cn.sf.bean.beans.Response;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by nijianfeng on 17/6/25.
 */
@Slf4j
public final class OssObjectUtil extends OssEnvInit {

     // 根据fileId从OSS获取文件对象
    public Response<OSSObject> getObject(BizOssEnum bizEnum, String fileId) {
        if(StringUtils.isBlank(fileId)){
            throw new IllegalArgumentException("getOSSObject ---> fileId is null!!!");
        }
        BucketEnum key = checkBizEnum(bizEnum);
        try {
            //1.获取上传AccessKey  配置RAM用户AK信息
            OssProperty accessData = accessDataMap.get(key.getAccess());
            //2.获取OSSClient
            OSSClient ossClient = clientsMap.get(key.getAccess());
            OSSObject ossObject = ossClient.getObject(accessData.getBucket(), fileId);
            log.error("getObject success --->   access:{},fileId:{} ",key.getAccess(),fileId);
            return Response.ok(ossObject);
        }catch(OSSException oe) {
            log.error("getObject OSSException --->   fileId:{} Error Message:{} Error Code:{} Request ID:{} Host ID:{}",
                    fileId, oe.getErrorMessage(),oe.getErrorCode(),oe.getRequestId(),oe.getHostId(),oe);
            return Response.fail("获取文件发生OSSException异常");
        }catch(ClientException ce) {
            log.error("getObject ClientException --->   fileId:{} Error Message:{} Error Code:{},requestId:{}",
                    fileId, ce.getErrorMessage(),ce.getErrorCode(),ce.getRequestId(),ce);
            return Response.fail("获取文件发生ClientException异常");
        } catch (Exception e) {
            log.error("getObject Exception --->   desc:{},fileId:{}", key.getDesc(), fileId, e);
            return Response.fail("OSS.getObject.Exception");
        }
    }

    // 上传文件到相应的bucket
    public Response<String> putObject(OssRequest ossRequest) {
        if(ossRequest==null){
            throw new IllegalArgumentException("putObject ---> ossRequest is null!!!");
        }
        BucketEnum key = checkBizEnum(ossRequest.getBizEnum());
        InputStream inputStream = ossRequest.getInputStream();
        int bizCode = ossRequest.getBizEnum().getBizCode();
        String fileType = ossRequest.getFileType();
        String description = ossRequest.getDescription();
        try {
            //1.获取上传AccessKey
            OssProperty accessData = accessDataMap.get(key.getAccess());
            //2.获取OSSClient
            OSSClient ossClient = clientsMap.get(key.getAccess());

            String fileBizPath = ossRequest.getBizEnum().getFilePath();
            //获取文件后缀  .jpg
            String suffix;
            String fileName = ossRequest.getFileName().replace(",","").replaceAll("\\s","");//去掉空格和逗号
            if(fileName.lastIndexOf(".")>-1) {
                suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            }else{
                suffix = "";
            }
            //拼装path
            String filePath = fileBizPath+"/";
            String fileId = filePath+ UUID.randomUUID().toString()+suffix;
            //提前获取文件大小,oss上传成功会把输入流关闭
            Long fileSize = Long.valueOf(inputStream.available());
            //发送文件到oss
            ObjectMetadata meta = new ObjectMetadata() ;
            if(ossRequest.isAttachment()){
                String fileRealEncode = URLEncoder.encode(fileName, "utf-8");
                meta.setContentDisposition("attachment;filename="+fileRealEncode+";filename*=UTF-8''"+fileRealEncode);
            }
            meta.setContentLength(fileSize);
            PutObjectResult result = ossClient.putObject(accessData.getBucket(), fileId, inputStream, meta);
            log.info("putObject success --->  fileName:{},bizCode:{},fileType:{},description:{},fileSize:{},eTag:{},access:{}",
                        fileName, bizCode, fileType, description, fileSize, result.getETag(),key.getAccess());
            return Response.ok(fileId+","+result.getETag());
        } catch (IOException e) {
            log.error("putObject IOException --->   bizCode:{}",bizCode,e);
            return Response.fail("OSS.putObject.IOException");
        }catch (Exception e) {
            log.error("putObject Exception --->   bizCode:{}",bizCode,e);
            return Response.fail("OSS.putObject.Exception");
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("putObject closed InputStream IOException --->   bizCode:{}",bizCode,e);
                }
            }
        }
    }

    // 根据fileId删除文件
    public boolean deleteObject(BizOssEnum bizEnum, String fileId){
        if(StringUtils.isBlank(fileId)){
            throw new IllegalArgumentException("deleteObject ---> fileId is null!!!");
        }
        BucketEnum key = checkBizEnum(bizEnum);
        try {
            //1.获取上传AccessKey
            OssProperty accessData = accessDataMap.get(key.getAccess());
            //2.获取OSSClient
            OSSClient ossClient = clientsMap.get(key.getAccess());
            ossClient.deleteObject(accessData.getBucket(), fileId);
            log.error("deleteObject success --->   access:{},fileId:{} ",key.getAccess(),fileId);
            return true;//正常返回
        }catch(OSSException oe) {
            log.error("deleteObject OSSException --->   Error Message:{} Error Code:{} Request ID:{} Host ID:{}",
                    oe.getErrorMessage(),oe.getErrorCode(),oe.getRequestId(),oe.getHostId(),oe);
        }catch(ClientException ce) {
            log.error("deleteObject ClientException --->   Error Message:{} Error Code:{} Request ID:{} Host ID:{}",
                    ce.getErrorMessage(),ce.getErrorCode(),ce.getRequestId(),ce);
        }catch(Exception e){
            log.error("deleteObject Exception --->   fileId:{}",fileId,e);
        }
        return false;
    }

}
