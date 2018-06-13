package cn.sf.compiler.rap.utils;

import cn.sf.compiler.rap.RapClazz;
import cn.sf.compiler.rap.RapField;
import cn.sf.compiler.rap.RapMethod;
import cn.sf.compiler.rap.RapParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Arrays;

/**
 * Created by nijianfeng on 18/4/30.
 */
public class AnnotationUtil {

    public static boolean isController(Element rap) {
        if (rap.getKind() == ElementKind.CLASS) {
            Controller controller = rap.getAnnotation(Controller.class);
            if (controller != null) {
                return true;
            }
            RestController restController = rap.getAnnotation(RestController.class);
            if (restController != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseBody(Element rap) {
        if (rap.getKind() == ElementKind.METHOD || rap.getKind() == ElementKind.CLASS) {
            ResponseBody responseBody = rap.getAnnotation(ResponseBody.class);
            if (responseBody != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRapClazz(Element rap) {
        if (rap.getKind() == ElementKind.CLASS) {
            RapClazz rapClazz = rap.getAnnotation(RapClazz.class);
            if (rapClazz != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRapMethod(Element rap) {
        if (rap.getKind() == ElementKind.METHOD && rap.getModifiers().contains(Modifier.PUBLIC)) {
            RapMethod rapMethod = rap.getAnnotation(RapMethod.class);
            if (rapMethod != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRapParam(Element rap) {
        if (rap.getKind() == ElementKind.PARAMETER) {
            RapParam rapParam = rap.getAnnotation(RapParam.class);
            if (rapParam != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRapField(Element rap) {
        if (rap.getKind() == ElementKind.FIELD) {
            RapField rapField = rap.getAnnotation(RapField.class);
            if (rapField != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRequestParam(Element rap) {
        if (rap.getKind() == ElementKind.PARAMETER) {
            RequestParam requestParam = rap.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRequestBody(Element rap) {
        if (rap.getKind() == ElementKind.PARAMETER) {
            RequestBody requestBody = rap.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRequestMapping(Element rap) {
        if (rap.getKind() == ElementKind.METHOD || rap.getKind() == ElementKind.CLASS) {
            RequestMapping requestMapping = rap.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                return true;
            }
        }
        return false;
    }

    public static String getRequestMappingValue(Element rap) {
        if (rap.getKind() == ElementKind.METHOD || rap.getKind() == ElementKind.CLASS) {
            RequestMapping requestMapping = rap.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] values = requestMapping.value();
                if (values.length == 0) {
                    return "/";
                }
                return StringUtil.removeArray(Arrays.toString(requestMapping.value()));
            }
        }
        return "/";
    }

    public static String getRequestMappingMethod(Element rap) {
        if (rap.getKind() == ElementKind.METHOD || rap.getKind() == ElementKind.CLASS) {
            RequestMapping requestMapping = rap.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                RequestMethod[] methods = requestMapping.method();
                if (methods.length == 0) {
                    return "";
                }
                return StringUtil.removeArray(Arrays.toString(requestMapping.method()));
            }
        }
        return "";
    }

}
