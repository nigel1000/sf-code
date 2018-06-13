package cn.sf.project.controller.office;

import cn.sf.auto.aop.print.AutoLog;
import com.google.common.collect.Maps;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 缓存管理
 */
@Controller
@RequestMapping(value = "/office", method = {RequestMethod.GET, RequestMethod.POST})
@Slf4j
@AutoLog
public class OfficeController {

    @RequestMapping(value = "/open", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String openOffice(HttpServletRequest request, @NonNull String filePath) throws Exception {
        // ******************************卓正PageOffice组件的使用*******************************
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        poCtrl.setServerPage(request.getContextPath() + "/poserver.zz"); // 此行必须
        // 隐藏菜单栏
        // poCtrl1.setMenubar(false);
        // 添加自定义按钮
        // poCtrl1.addCustomToolButton("插入书签", "addBookMark", 5);
        // poCtrl1.addCustomToolButton("删除书签", "delBookMark", 5);
        poCtrl.webOpen(filePath, OpenModeType.docNormalEdit, "张三");
        String content = poCtrl.getHtmlCode("PageOfficeCtrl1");
        // String fileString = new
        // String(Files.readAllBytes(ResourceUtils.getFile("classpath:office/template.ftl").toPath()),
        // Charset.forName("UTF-8"));
        // fileString = fileString.replace("${content}", content);
        // 获取文档模板
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("UTF-8");
        configuration
                .setDirectoryForTemplateLoading(ResourceUtils.getFile("classpath:office/template.ftl").getParentFile());
        Template template = configuration.getTemplate("template.ftl");
        // 填充模板生成输出流
        Map<String, Object> dataModelMap = Maps.newHashMap();
        dataModelMap.put("content", content);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        template.process(dataModelMap, out);
        // 将输出流刷新到outputStream中
        out.flush();
        return new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
    }


}
