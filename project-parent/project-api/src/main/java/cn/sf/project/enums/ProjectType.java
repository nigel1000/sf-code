package cn.sf.project.enums;

import cn.sf.project.enums.base.BaseEnum;

import java.util.HashMap;
import java.util.Map;

public enum ProjectType implements BaseEnum<ProjectType> {
    /**
     * 异常
     */
    NULL(-1, "异常状态"),
    /**
     * 状态枚举
     */
    NO(0, "停用"), YES(1, "启用"),

    ;

    // 值
    private final int key;
    // 描述
    private final String desc;

    private static Map<Integer, ProjectType> map = new HashMap<>();
    static {
        for (ProjectType item : ProjectType.values()) {
            map.put(item.getKey(), item);
        }
    }

    // 构造函数
    ProjectType(int v, String d) {
        key = v;
        desc = d;
    }

    @Override
    public ProjectType genEnumByKey(Integer key) {
        return map.get(key) == null ? map.get(-1) : map.get(key);
    }
    @Override
    public int getKey() {
        return key;
    }
    @Override
    public String getDesc() {
        return desc;
    }
}
