package cn.sf.excel.base;

/**
 * Created by nijianfeng on 17/3/22.
 */
public interface BaseEnum<T extends BaseEnum> {

    T getEnumByDesc(String descValue);

    default String getDesc(){
        return "NULL";
    }

    default int getValue(){
        return -1;
    }

}
