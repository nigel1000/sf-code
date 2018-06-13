package cn.sf.shiro.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 17/7/4.
 */
@Data
@Component
@ConfigurationProperties(
        prefix = "shiro",
        ignoreInvalidFields = true
)
public class ShiroProperty {

    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;
    private String urlFilters;
    List<String> shiroAnnotations;
    //譬如logout  org.apache.shiro.web.filter.authc.LogoutFilter
    private Map<String,FilterProperty> filters;


    @ToString
    @Data
    public static class FilterProperty {
        //过滤器类名
        private String className;
        //登录请求
        private String loginRequest;
        //不验证的urls
        private List<String> anonUrls;
    }

}
