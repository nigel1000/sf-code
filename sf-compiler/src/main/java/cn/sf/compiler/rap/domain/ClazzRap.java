package cn.sf.compiler.rap.domain;

import cn.sf.compiler.rap.RapClazz;
import cn.sf.compiler.rap.utils.AnnotationUtil;
import com.alibaba.fastjson.annotation.JSONType;
import com.google.common.collect.Lists;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.util.List;

/**
 * Created by nijianfeng on 18/4/30.
 */
@Data
@JSONType(ignores = {"classEle"})
public class ClazzRap {

    private String clazzName;
    private String modulePath;

    private String requestType;
    private String prefixUrl;
    private Boolean isResponseBody;

    private List<MethodRap> methodRapList;

    private TypeElement classEle;

    public ClazzRap(TypeElement classEle) {
        this.setClassEle(classEle);

        this.setClazzName(classEle.getQualifiedName().toString());
        this.setIsResponseBody(AnnotationUtil.isResponseBody(classEle));
        this.setModulePath(File.separator + classEle.getAnnotation(RapClazz.class).modulePath());
        this.setPrefixUrl(AnnotationUtil.getRequestMappingValue(classEle));
        this.setRequestType(AnnotationUtil.getRequestMappingMethod(classEle));

        List<MethodRap> methodRapList = Lists.newArrayList();
        // 遍历Class内所有元素
        for (Element methodEle : classEle.getEnclosedElements()) {
            if (AnnotationUtil.hasRequestMapping(methodEle) && AnnotationUtil.hasRapMethod(methodEle)) {
                methodRapList.add(new MethodRap((ExecutableElement) methodEle));
            }
        }
        this.setMethodRapList(methodRapList);
    }


}
