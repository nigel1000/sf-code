package cn.sf.redis.enums;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public enum BizRedisEnum {
    NULL(-1, "null","not_normal", -1),

    /**
     * 通用模块
     */
    DEFAULT(0,"默认空间", "zcy_default",60*60*24*30),
    /**
     * 用户模块
     */
    USER_MODULE(1, "用户模块","user_module",60*60*24*30),

    /**
     * **模块
     */

    ;

    private final int bizCode;
    private final String bizDesc;
    private final String groupName;
    private final int expireTime;

    private static Map<Integer, BizRedisEnum> map = Maps.newHashMap();

    static {
        for (BizRedisEnum item : BizRedisEnum.values()) {
            map.put(item.getBizCode(), item);
        }
    }

    /**
     * 构造函数
     */
    BizRedisEnum(int bizCode,String bizDesc, String groupName, int expireTime) {
        this.bizCode = bizCode;
        this.bizDesc = bizDesc;
        this.groupName = groupName;
        this.expireTime = expireTime;
    }

    public static BizRedisEnum genEnumByKey(int key) {
        return map.get(key) == null ? map.get(-1) : map.get(key);
    }

    public int getBizCode() {
        return this.bizCode;
    }
    public String getBizDesc() {
        return this.bizDesc;
    }
    public String getGroupName() {
        return this.groupName;
    }
    public int getExpireTime() {
        return this.expireTime;
    }

    public Set<String> getAllKeyPrefix() {
        Set<String> strs = Sets.newHashSet();
        for (BizRedisEnum item : BizRedisEnum.values()) {
            if (!item.equals(BizRedisEnum.NULL)){
                strs.add(item.getGroupName()+"-"+item.getExpireTime());
            }
        }
        return strs;
    }

}
