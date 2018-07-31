package cn.sf.compiler.rap.eval;

import java.lang.reflect.Method;

/**
 * Created by nijianfeng on 18/7/31.
 */
public class EvalUtil {

    public static final String CLASS_NAME = "God";
    private static final String METHOD_NAME = "getInstance";

    private static final String CLASS_TEMPLATE;
    private static final String IMPORT_PACKAGE = "{initClassPackage}";
    private static final String INIT_OBJECT = "{initCode}";

    static {
        StringBuilder imports = new StringBuilder();
        imports.append("import com.google.common.collect.*;");

        StringBuffer codes = new StringBuffer();
        codes.append(imports.toString());
        codes.append(IMPORT_PACKAGE);
        codes.append("public class " + CLASS_NAME + "{");
        codes.append("    public Object " + METHOD_NAME + "(){");
        codes.append("        return " + INIT_OBJECT + ";");
        codes.append("    }");
        codes.append("}");
        CLASS_TEMPLATE = codes.toString();
    }

    public static Object eval(String initClassPackage, String initCode) throws Exception {

        // 调用自定义类加载器加载编译在内存中class文件
        Class<?> clazz = new EvalClassLoad()
                .findClass(CLASS_TEMPLATE.replace(IMPORT_PACKAGE, initClassPackage == null ? "" : initClassPackage)
                        .replace(INIT_OBJECT, initCode == null ? "null" : initCode));
        Method method = clazz.getMethod(METHOD_NAME);
        // 通过反射调用方法
        return method.invoke(clazz.newInstance());

    }

    // public static void main(String[] args) throws Exception {
    // System.out.println(eval(null, "Lists.newArrayList(1,3,4)"));
    // System.out.println(eval(null, "Lists.newArrayList(1,3,4)"));
    // }

}
