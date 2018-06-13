package cn.sf.project;

import cn.sf.auto.aop.config.EnableExcpReturn;
import cn.sf.bean.beans.Response;
import com.zhuozhengsoft.pageoffice.poserver.AdminSeal;
import com.zhuozhengsoft.pageoffice.poserver.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by nijianfeng on 17/6/10.
 */
@ImportResource(value = {"classpath:/spring/consumer.xml"})
@ComponentScan(basePackages = {"cn.sf.project"})
@EnableAutoConfiguration(exclude = {FreeMarkerAutoConfiguration.class})
@Configuration
@EnableExcpReturn(returnTypes = {Response.class}, methodNames = {"fail"})
public class WebApp implements EmbeddedServletContainerCustomizer {

    public static void main(String args[]) {
        SpringApplication.run(WebApp.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
        // container.addErrorPages(new ErrorPage(java.lang.Throwable.class,"/error/500"));
    }


    @Bean
    public ServletContextInitializer initializer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                // servletContext.setInitParameter("posetup_downloadurl", "download url");
                ServletRegistration.Dynamic serverServlet = servletContext.addServlet("poserver", new Server());
                serverServlet.setLoadOnStartup(1);
                serverServlet.addMapping("/poserver.zz", "/sealsetup.exe", "/posetup.exe", "/pageoffice.js",
                        "/jquery.min.js", "/pobstyle.css");
                ServletRegistration.Dynamic adminSealServlet = servletContext.addServlet("adminseal", new AdminSeal());
                adminSealServlet.setLoadOnStartup(1);
                adminSealServlet.addMapping("/adminseal.zz", "/loginseal.zz", "", "/sealimage.zz");
            }
        };
    }

}
