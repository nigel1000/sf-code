package cn.sf.compiler.rap.eval;

import com.google.common.collect.Lists;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by nijianfeng on 18/7/31.
 */
public class EvalClassLoad extends ClassLoader {


    @Override
    protected Class<?> findClass(String content) throws ClassNotFoundException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 用于诊断源代码编译错误的对象
        // DiagnosticCollector diagnostics = new DiagnosticCollector();
        // Java标准文件管理器
        JavaFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
        // 内存中的源代码保存在一个从JavaFileObject继承的类中
        JavaFileObject file = new JavaSourceFromString(EvalUtil.CLASS_NAME, content);
        Iterable compilationUnits = Lists.newArrayList(file);
        // 建立一个编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        // 编译源程序
        boolean result = task.call();
        if (result) {
            return fileManager.getClassLoader(null).loadClass(EvalUtil.CLASS_NAME);
        }
        return null;
    }
}


class JavaSourceFromString extends SimpleJavaFileObject {

    /**
     * 源代码
     */
    private String content = "";
    /**
     * 用于存储class字节
     */
    private ByteArrayOutputStream outputStream;

    protected JavaSourceFromString(String javaFileName, String content) {
        super(_createUri(javaFileName), JavaFileObject.Kind.SOURCE);
        this.content = content;
    }

    protected JavaSourceFromString(String javaFileName, Kind kind) {
        super(_createUri(javaFileName), kind);
        this.outputStream = new ByteArrayOutputStream();
    }

    /**
     * 产生一个URL资源路径
     */
    private static URI _createUri(String javaFileName) {
        // 注意此处未设置包名
        return URI.create("string:///" + javaFileName.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension);
    }

    /**
     * 文本文件代码
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }

    @Override

    public OutputStream openOutputStream() throws IOException {
        return outputStream;
    }

    public byte[] getClassBytes() {
        return outputStream.toByteArray();
    }


}


class ClassFileManager extends ForwardingJavaFileManager {



    private JavaSourceFromString classFileObject;

    /**
     * 创建ForwardingJavaFileManager的新实例。
     */
    public ClassFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    /**
     * 获取要输出的JavaFileObject文件对象 代表给定位置中指定类名的指定类别。
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
            FileObject sibling) throws IOException {
        return classFileObject = new JavaSourceFromString(className, kind);
    }

    @Override
    // 获得一个定制ClassLoader，返回我们保存在内存的类
    public ClassLoader getClassLoader(Location location) {
        return new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                byte[] classBytes = classFileObject.getClassBytes();// 获取class文件对象的字节数组
                return super.defineClass(name, classBytes, 0, classBytes.length);// 定义Class对象
            }
        };
    }

}

