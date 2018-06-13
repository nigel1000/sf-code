package cn.sf.alibaba.udf;

import com.aliyun.odps.udf.UDF;
import org.apache.commons.lang.StringUtils;

public class SubJSonArrayUDF extends UDF {

    //jsonArray to index's object
    public String evaluate(String json, Long index) {
        if (StringUtils.isBlank(json)) {
            return StringUtils.EMPTY;
        }
        String[] str = json
                .replace("[","")
                .replace("]",",")
                .split("},");
        if (str.length<(index+1)) {
            return StringUtils.EMPTY;
        }
        return str[index.intValue()]+"}";
    }

}