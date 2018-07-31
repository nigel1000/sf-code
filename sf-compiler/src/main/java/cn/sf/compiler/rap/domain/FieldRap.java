package cn.sf.compiler.rap.domain;

import cn.sf.compiler.rap.RapField;
import cn.sf.compiler.rap.utils.AnnotationUtil;
import com.alibaba.fastjson.annotation.JSONType;
import com.google.common.collect.Lists;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.List;

/**
 * Created by nijianfeng on 18/4/30.
 */
@Data
@JSONType(ignores = {"clazzName", "fieldClazzEle"})
public class FieldRap {

    private String clazzName;
    private String fieldType;
    private String fieldName;
    private String fieldMeans;
    private String fieldMockValue;

    private Element fieldClazzEle;

    public static List<FieldRap> genFieldRapList(Element fieldClazzEle) {
        List<FieldRap> fieldRaps = Lists.newArrayList();
        for (Element innerEle : fieldClazzEle.getEnclosedElements()) {
            FieldRap fieldRap = new FieldRap();
            fieldRap.setFieldClazzEle(fieldClazzEle);
            if (innerEle.getKind() == ElementKind.FIELD) {
                fieldRap.setFieldName(innerEle.toString());
                fieldRap.setFieldType(innerEle.asType().toString());
                fieldRap.setClazzName(fieldClazzEle.toString());
                if (AnnotationUtil.hasRapField(innerEle)) {
                    RapField rapField = innerEle.getAnnotation(RapField.class);
                    if (rapField != null) {
                        fieldRap.setFieldMeans(rapField.fieldMeans());
                        fieldRap.setFieldMockValue(rapField.fieldMockValue());
                    }
                }
                fieldRaps.add(fieldRap);
            }
        }
        return fieldRaps;
    }

}
