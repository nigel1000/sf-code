package cn.sf.auto.mybatis.util;

import cn.sf.auto.exps.AutoCodeException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.util.ResourceUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class TemplateUtils {

    public static String genTemplate(String dir, String file, Map<String, Object> map) {
        try {
            // 获取文档模板
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setDirectoryForTemplateLoading(ResourceUtils.getFile(dir + file).getParentFile());
            Template template = configuration.getTemplate(file);
            // 填充模板生成输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            template.process(map, out);
            // 将输出流刷新到outputStream中
            out.flush();
            return new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw AutoCodeException.valueOf("模版生成有问题!", e);
        }

    }


}
