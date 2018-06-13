package cn.sf.alibaba.udf.test;

import cn.sf.alibaba.udf.SubJSonArrayUDF;

/**
 * Created by nijianfeng on 17/6/25.
 */
public class TestSubJsonArrayUDF {
    public static void main(String[] args) {
        String jsonArray = "[" +
                "{\"bxmc\":\"1\",\"cpsx\":\"节能,节水\",\"cz\":\"2\",\"dj\":\"1\",\"dw\":\"3\",\"ggxh\":\"1\",\"pmpp\":\"1\",\"sl\":\"2\",\"xh\":\"1\",\"zj\":\"3\"}," +
                "\"bxmc\":\"2\",\"cpsx\":\"节能,节水\",\"cz\":\"3\",\"dj\":\"2\",\"dw\":\"2\",\"ggxh\":\"2\",\"pmpp\":\"2\",\"sl\":\"2\",\"xh\":\"2\",\"zj\":\"4\"}," +
                "{\"bxmc\":\"3\",\"cpsx\":\"节水,环保\",\"cz\":\"3\",\"dj\":\"2\",\"dw\":\"3\",\"ggxh\":\"3\",\"pmpp\":\"2\",\"sl\":\"2\",\"xh\":\"3\",\"zj\":\"3\"}]"
        ;
        System.out.println(new SubJSonArrayUDF().evaluate( jsonArray,3L));
    }
}
