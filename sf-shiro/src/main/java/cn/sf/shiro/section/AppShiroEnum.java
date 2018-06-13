package cn.sf.shiro.section;

import java.util.HashMap;
import java.util.Map;

public enum AppShiroEnum {

    NULL(-1,"null"),

    CAR_MEDIA(575,"车媒体"),

    ;

    AppShiroEnum(int bizCode, String bizDesc){
        this.bizCode = bizCode;
        this.bizDesc = bizDesc;
    }

    private final int bizCode;
    private final String bizDesc;


    private static Map<Integer, AppShiroEnum> map = new HashMap<>();
    static {
        for (AppShiroEnum item : AppShiroEnum.values()) {
            map.put(item.getBizCode(), item);
        }
    }
    public AppShiroEnum genEnumByBizCode(int bizCode) {
        return map.get(bizCode);
    }

    public int getBizCode() {
        return bizCode;
    }

    public String getBizDesc() {
        return bizDesc;
    }
}