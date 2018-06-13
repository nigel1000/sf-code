package cn.sf.alibaba.udf;

import cn.sf.alibaba.enums.GoodsPropertyMapping;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.odps.udf.UDF;
import org.apache.commons.lang.StringUtils;

public class GoodsPropertyMappingUDF extends UDF {

    public String evaluate(String propertyJson) {

        if (StringUtils.isBlank(propertyJson)) {
            return "";
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(propertyJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        for (GoodsPropertyMapping goodsPropertyMapping : GoodsPropertyMapping.values()) {
            String zcyCode = goodsPropertyMapping.getZcyCode();
            if ("".equals(zcyCode)) {
                continue;
            }
            Object o = jsonObject.get(zcyCode);
            if (o != null) {
                return GoodsPropertyMapping.getOfficialCode(zcyCode);
            }
        }
        return GoodsPropertyMapping.getOfficialCode("");
    }
}
