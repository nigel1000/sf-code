package cn.sf.project.converts;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Long的长度是64位，而前端js的number的精度只有52位，转成String防止精度丢失
 */
public class Long2StringConverter extends StdConverter<Long, String> {
    @Override
    public String convert(Long value) {
        if (null == value)
            return null;
        return value.toString();
    }
}