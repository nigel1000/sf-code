package cn.sf.netty.demo.httpxml.model;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

//它包含两个成员变量：FullHttpResponse和Object，Object就是业务需要发送的应答POJO对象。
@Data
public class HttpXmlResponse {
    private FullHttpResponse httpResponse;
    private Object result;
    public HttpXmlResponse(FullHttpResponse httpResponse, Object result) {
        this.httpResponse = httpResponse;
        this.result = result;
    }
}