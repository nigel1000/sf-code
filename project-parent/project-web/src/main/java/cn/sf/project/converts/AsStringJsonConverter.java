package cn.sf.project.converts;

import cn.sf.utils.json.Json;
import com.fasterxml.jackson.databind.util.StdConverter;

public class AsStringJsonConverter extends StdConverter<Object, String> {
    @Override
    public String convert(Object value) {
        if (null == value)
            return null;
        if (value instanceof String)
            return (String) value;
        if (value instanceof Number)
            return value.toString();
        return Json.of(value, null);
    }
}