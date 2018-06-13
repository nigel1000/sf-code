package cn.sf.compiler.rap;

import cn.sf.bean.excps.KnowException;
import cn.sf.compiler.rap.domain.ClazzRap;
import cn.sf.compiler.rap.domain.FieldRap;
import cn.sf.compiler.rap.domain.MethodRap;
import cn.sf.compiler.rap.domain.ParamRap;
import cn.sf.compiler.rap.utils.AnnotationUtil;
import cn.sf.compiler.rap.utils.FileUtil;
import cn.sf.compiler.rap.utils.JsonUtil;
import cn.sf.compiler.rap.utils.StringUtil;
import com.google.auto.service.AutoService;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nijianfeng on 18/4/30.
 */
@AutoService(Processor.class)
public class RapProcessor extends AbstractProcessor {

    private String filePath;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        try {
            URL targetUrl = RapProcessor.class.getClassLoader().getResource("");
            if (targetUrl == null) {
                error("获取targetUrl失败!");
            } else {
                String targetPath = targetUrl.getPath();
                String basePath = targetPath.substring(0, targetPath.indexOf("target"));
                filePath = StringUtil.removeDoublePath(basePath + File.separator + "docs" + File.separator);
            }
        } catch (Exception e) {
            error("获取filePath失败!");
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "RapProcessor filePath:" + filePath);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = Sets.newHashSet();
        types.add(RapClazz.class.getCanonicalName());
        types.add(RapMethod.class.getCanonicalName());
        types.add(RapParam.class.getCanonicalName());
        types.add(RapField.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Map<String, ClazzRap> clazzRapMap = Maps.newHashMap();
    private Map<String, List<FieldRap>> fieldRapsMap = Maps.newHashMap();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            // roundEnv.getRootElements()会返回工程中所有的Class
            for (Element classEle : roundEnv.getRootElements()) {
                if (!AnnotationUtil.isController(classEle)) {
                    continue;
                }
                if (!AnnotationUtil.hasRapClazz(classEle)) {
                    continue;
                }
                ClazzRap clazzRap = new ClazzRap((TypeElement) classEle);
                String clazzName = clazzRap.getClazzName();
                clazzRapMap.putIfAbsent(clazzName, clazzRap);
            }
            // 遍历所有被注解了@RapField的元素
            Set<? extends Element> fieldEles = roundEnv.getElementsAnnotatedWith(RapField.class);
            for (Element fieldEle : fieldEles) {
                Element fieldClazzEle = fieldEle.getEnclosingElement();
                String clazzName = fieldClazzEle.toString();
                if (fieldRapsMap.get(clazzName) != null) {
                    continue;
                }
                fieldRapsMap.putIfAbsent(clazzName, FieldRap.genFieldRapList(fieldClazzEle));
            }
            String dirPath = StringUtil.removeDoublePath(filePath + File.separator);
            // 生成源数据文件
            Map<String, String> dataMap = Maps.newHashMap();
            dataMap.put("clazzRapData", JsonUtil.obj2Json(clazzRapMap));
            dataMap.put("fieldRapData", JsonUtil.obj2Json(fieldRapsMap));
            FileUtil.genOriginFile(dirPath + File.separator + "注解处理器源数据", dataMap);
            // 生成api文件
            genApiMd();

        } catch (KnowException e) {
            error(e.getErrorMessage());
            return true;
        } catch (Exception e) {
            error(e.getMessage());
            return true;
        }

        return false;
    }

    private void genApiMd() {
        for (String key : clazzRapMap.keySet()) {
            ClazzRap clazzRap = clazzRapMap.get(key);
            String dirPath = StringUtil.removeDoublePath(filePath + File.separator + clazzRap.getModulePath());
            List<MethodRap> methodRapList = clazzRap.getMethodRapList();
            for (MethodRap methodRap : methodRapList) {
                Map<String, String> dataMap = Maps.newHashMap();
                String requestType = clazzRap.getRequestType();
                if (!StringUtils.isEmpty(methodRap.getRequestType())) {
                    requestType = methodRap.getRequestType();
                }
                dataMap.put("requestType", requestType);
                String requestUrl = "/";
                if (!StringUtils.isEmpty(methodRap.getSuffixUrl())) {
                    List<String> prefixUrls =
                            Splitter.on(",").trimResults().omitEmptyStrings().splitToList(clazzRap.getPrefixUrl());
                    List<String> suffixUrls =
                            Splitter.on(",").trimResults().omitEmptyStrings().splitToList(methodRap.getSuffixUrl());
                    for (String prefixUrl : prefixUrls) {
                        requestUrl += prefixUrl;
                        for (String suffixUrl : suffixUrls) {
                            requestUrl += "/" + suffixUrl + ",";
                        }
                    }
                }
                dataMap.put("requestUrl", StringUtil.removeLast(StringUtil.removeDoublePath(requestUrl)));
                List<ParamRap> paramRapList = methodRap.getParamRapList();
                String requestParam = "";
                String requestJson = "";
                for (ParamRap paramRap : paramRapList) {
                    requestParam += "|" + paramRap.getParamName() + "|" + paramRap.getParamMeans() + "|"
                            + paramRap.getParamType() + "|" + paramRap.getIsRequestBody() + "|"
                            + System.lineSeparator();
                    requestJson = genJsonByClassName(paramRap.getParamType());
                }
                dataMap.put("requestParam", requestParam);
                dataMap.put("requestJson", requestJson);
                Boolean isResponseJson = clazzRap.getIsResponseBody();
                if (!isResponseJson) {
                    isResponseJson = methodRap.getIsResponseBody();
                }
                String responseResult = "|" + methodRap.getReturnType() + "|" + isResponseJson + "|";
                String responseJson = "";
                if (isResponseJson) {
                    responseJson = genJsonByClassName(methodRap.getReturnType());
                }
                dataMap.put("responseResult", responseResult);
                dataMap.put("responseJson", responseJson);

                String methodPtah = dirPath + File.separator;
                if (!StringUtils.isEmpty(methodRap.getModulePath().trim())) {
                    methodPtah += methodRap.getModulePath() + File.separator;
                }
                FileUtil.map2File(methodPtah + File.separator + methodRap.getMethodMeans(), dataMap);
            }
        }
    }

    private String genJsonByClassName(String clazzName) {
        return JsonUtil.obj2Json(getAllClazzNames(getClazzNames(clazzName)));
    }

    private Map<String, List<FieldRap>> getAllClazzNames(Set<String> clazzNames) {
        Map<String, List<FieldRap>> resultMaps = Maps.newHashMap();
        for (String name : clazzNames) {
            List<FieldRap> fieldRaps = fieldRapsMap.get(name);
            if (CollectionUtils.isEmpty(fieldRaps)) {
                continue;
            }
            resultMaps.putIfAbsent(name, fieldRaps);
            for (FieldRap fieldRap : fieldRaps) {
                Set<String> fieldTypes = getClazzNames(fieldRap.getFieldType());
                if (CollectionUtils.isEmpty(fieldTypes)) {
                    continue;
                }
                for (String fieldType : fieldTypes) {
                    if (resultMaps.get(fieldType) != null) {
                        continue;
                    }
                    if (fieldRapsMap.get(fieldType) != null) {
                        resultMaps.putIfAbsent(fieldType, getAllClazzNames(fieldTypes).get(fieldType));
                    }
                }
            }
        }
        return resultMaps;
    }

    private Set<String> getClazzNames(String clazzName) {
        Set<String> clazzNames = Sets.newHashSet();
        char[] chars = clazzName.toCharArray();
        List<Character> origins = Lists.newArrayList('.', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
        StringBuilder tmpName = new StringBuilder();
        for (char one : chars) {
            if (origins.contains(one)) {
                tmpName.append(one);
            } else {
                clazzNames.add(tmpName.toString());
                tmpName = new StringBuilder();
            }
        }
        clazzNames.add(tmpName.toString());
        return clazzNames;
    }


    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

}
