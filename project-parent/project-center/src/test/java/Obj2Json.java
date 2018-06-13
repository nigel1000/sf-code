import cn.sf.bean.beans.Response;
import cn.sf.project.domain.Project;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Obj2Json {


    public static void main(String[] args) {

        Obj2Json.obj2Json(Response.ok(new Project()));
    }


    private static void obj2Json(Class clazz){
        try {
            Object obj = clazz.newInstance();
            String jsonStr = JSONArray.toJSONString(obj,new SerializerFeature[]{
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat,
                    SerializerFeature.WriteNullNumberAsZero,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullBooleanAsFalse
            });
            System.out.println(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void obj2Json(Object obj){
        try {
            String jsonStr = JSONArray.toJSONString(obj,new SerializerFeature[]{
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat,
                    SerializerFeature.WriteNullNumberAsZero,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullBooleanAsFalse
            });
            System.out.println(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}