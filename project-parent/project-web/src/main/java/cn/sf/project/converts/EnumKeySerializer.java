package cn.sf.project.converts;

import cn.sf.project.enums.base.BaseEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EnumKeySerializer extends JsonSerializer<BaseEnum> {

    @Override
    public void serialize(BaseEnum baseEnum, JsonGenerator gen, SerializerProvider provider)
            throws IOException{
        if(baseEnum!=null) {
            gen.writeString(baseEnum.getKey() + "->" + baseEnum.getDesc());
        }
    }

}