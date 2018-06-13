package cn.sf.compiler.rap.utils;

import cn.sf.bean.excps.KnowException;
import lombok.NonNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by nijianfeng on 18/4/30.
 */
public class FileUtil {


    public static synchronized void genDirIfAbsent(@NonNull Path path, boolean isCover) {
        File targetPath = path.toFile();
        if (!targetPath.exists()) {
            if (!targetPath.mkdirs()) {
                throw KnowException.valueOf("创建目录失败!" + path.toFile().getPath());
            }
        } else {
            if (isCover) {
                if (!targetPath.delete()) {
                    throw KnowException.valueOf("删除目录失败!" + path.toFile().getPath());
                }
                genDirIfAbsent(path, Boolean.FALSE);
            }
        }
    }

    public static String getString(@NonNull InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String s; // 依次循环，至到读的值为空
            while ((s = reader.readLine()) != null) {
                sb.append(s);
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    private static final String template;
    static {
        try {
            template = getString(FileUtil.class.getResourceAsStream(File.separator + "template.ftl"));
        } catch (Exception e) {
            throw KnowException.valueOf("读取模板失败!");
        }
    }

    public static void map2File(String fileName, Map<String, String> dataMap) {
        try {
            String template = FileUtil.template;
            for (String key : dataMap.keySet()) {
                template = template.replace("#{" + key + "}", dataMap.get(key));
            }
            genFile(fileName, template.getBytes(), Boolean.FALSE);
        } catch (Exception e) {
            throw KnowException.valueOf("生成文件失败!" + fileName);
        }
    }

    public static void genFile(String fileName, byte[] data, Boolean isCover) {
        try {
            String path = StringUtil.removeDoublePath(fileName) + ".md";
            if (!isCover) {
                if (Paths.get(path).toFile().exists()) {
                    return;
                }
            }
            FileUtil.genDirIfAbsent(Paths.get(path).getParent(), Boolean.FALSE);
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            throw KnowException.valueOf("生成文件失败!" + fileName);
        }
    }

    public static void genOriginFile(String fileName, Map<String, String> dataMap) {
        try {
            String template = getString(FileUtil.class.getResourceAsStream(File.separator + "注解处理器源数据.ftl"));
            for (String key : dataMap.keySet()) {
                template = template.replace("#{" + key + "}", dataMap.get(key));
            }
            genFile(fileName, template.getBytes(), Boolean.TRUE);
        } catch (Exception e) {
            throw KnowException.valueOf("生成文件失败!" + fileName);
        }
    }

}
