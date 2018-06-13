package cn.sf.compiler.rap.domain;

import cn.sf.compiler.rap.RapParam;
import cn.sf.compiler.rap.utils.AnnotationUtil;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.VariableElement;

/**
 * Created by nijianfeng on 18/4/30.
 */
@Data
@JSONType(ignores = {"paramEle"})
public class ParamRap {

    private String paramType = "--";
    private String paramName = "--";
    private String paramMeans = "--";

    private Boolean isRequestBody;

    private VariableElement paramEle;

    public ParamRap(VariableElement paramEle) {
        this.setParamEle(paramEle);

        if (AnnotationUtil.hasRequestParam(paramEle)) {
            RequestParam requestParam = paramEle.getAnnotation(RequestParam.class);
            this.setParamName(requestParam.name());
        } else {
            this.setParamName(paramEle.toString());
        }
        this.setParamType(paramEle.asType().toString());
        if (AnnotationUtil.hasRapParam(paramEle)) {
            RapParam rapParam = paramEle.getAnnotation(RapParam.class);
            this.setParamMeans(rapParam.paramMeans());
        }
        this.setIsRequestBody(AnnotationUtil.isRequestBody(paramEle));
    }
}
