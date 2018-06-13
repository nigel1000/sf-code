package cn.sf.compiler.rap.domain;

import cn.sf.compiler.rap.RapMethod;
import cn.sf.compiler.rap.utils.AnnotationUtil;
import com.alibaba.fastjson.annotation.JSONType;
import com.google.common.collect.Lists;
import lombok.Data;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * Created by nijianfeng on 18/4/30.
 */
@Data
@JSONType(ignores = {"methodEle"})
public class MethodRap {

    private String requestType;
    private String suffixUrl;
    private Boolean isResponseBody;

    private String clazzName;
    private String methodName;
    private String returnType;

    private String methodMeans;
    private String modulePath;

    private List<ParamRap> paramRapList;

    private ExecutableElement methodEle;

    public MethodRap(ExecutableElement methodEle) {
        this.setMethodEle(methodEle);

        RapMethod rapMethod = methodEle.getAnnotation(RapMethod.class);
        this.setModulePath(rapMethod.modulePath());
        this.setMethodMeans(rapMethod.methodMeans());
        this.setClazzName(clazzName);
        this.setIsResponseBody(AnnotationUtil.isResponseBody(methodEle));
        this.setMethodName(methodEle.toString());
        this.setRequestType(AnnotationUtil.getRequestMappingMethod(methodEle));
        this.setReturnType(methodEle.getReturnType().toString());
        this.setSuffixUrl(AnnotationUtil.getRequestMappingValue(methodEle));
        List<ParamRap> paramRapList = Lists.newArrayList();
        List<? extends VariableElement> params = methodEle.getParameters();
        for (VariableElement paramEle : params) {
            paramRapList.add(new ParamRap(paramEle));
        }
        this.setParamRapList(paramRapList);

    }
}
