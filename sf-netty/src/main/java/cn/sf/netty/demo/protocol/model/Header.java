package cn.sf.netty.demo.protocol.model;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public final class Header {
    private int crcCode = 0xabef0101;
    private int length;// 消息长度
    private long sessionID;// 会话ID
    private byte type;// 消息类型
    private byte priority;// 消息优先级
    private Map<String,Object> attachment = Maps.newHashMap(); // 附件

    @Override
    public String toString() {
        return "Header [crcCode=" + crcCode + ", length=" + length
                + ", sessionID=" + sessionID + ", type=" + type + ", priority="
                + priority + ", attachment=" + attachment + "]";
    }
}