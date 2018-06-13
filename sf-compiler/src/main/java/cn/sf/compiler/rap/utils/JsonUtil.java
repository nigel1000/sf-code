package cn.sf.compiler.rap.utils;

import cn.sf.bean.excps.KnowException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.NonNull;

public class JsonUtil {

    public static String obj2Json(@NonNull Object obj) {
        try {
            String jsonStr = JSONArray.toJSONString(obj,
                    new SerializerFeature[] {SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullNumberAsZero,
                            SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty,
                            SerializerFeature.WriteNullBooleanAsFalse});
            return jsonStr;
        } catch (Exception e) {
            throw KnowException.valueOf("obj2json转换失败!" + obj.getClass().getName());
        }
    }
}
