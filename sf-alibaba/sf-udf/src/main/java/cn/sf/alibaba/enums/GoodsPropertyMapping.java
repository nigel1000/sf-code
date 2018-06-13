package cn.sf.alibaba.enums;

import java.util.HashMap;
import java.util.Map;

public enum GoodsPropertyMapping {

    ENVIRON_PROTECT("environ", "环保", "cpsx003", "环保"),

    ENERGY_SAVING("energy", "节能(节水)", "cpsx004", "节能，节水"),

    ENERGY_ENVIRON("", "节能(节水)，环保", "cpsx007", "节能，节水，环保");

    private static Map<String, String> zCode2oCode = new HashMap<>();

    static {
        for (GoodsPropertyMapping item : GoodsPropertyMapping.values()) {
            zCode2oCode.put(item.getZcyCode(), item.getOfficialCode());
        }
    }

    public static String getOfficialCode(String zcyCode) {
        return zCode2oCode.get(zcyCode);
    }

    private final String zcyCode;
    private final String zcyName;
    private final String officialCode;
    private final String officialName;

    GoodsPropertyMapping(String zcyCode, String zcyName, String officialCode, String officialName) {
        this.zcyCode = zcyCode;
        this.zcyName = zcyName;
        this.officialCode = officialCode;
        this.officialName = officialName;
    }

    public String getZcyCode() {
        return zcyCode;
    }

    public String getZcyName() {
        return zcyName;
    }

    public String getOfficialCode() {
        return officialCode;
    }

    public String getOfficialName() {
        return officialName;
    }
}
