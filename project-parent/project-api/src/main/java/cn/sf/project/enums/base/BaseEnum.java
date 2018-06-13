package cn.sf.project.enums.base;

/**
 * Created by nijianfeng on 17/6/24.
 */
public interface BaseEnum<T> {
    T genEnumByKey(Integer key);
    int getKey();
    String getDesc();
}
