package cn.sf.shiro.section;

import cn.sf.bean.constants.LogString;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("urlFilterSection")
@Slf4j
public class UrlFilterSection implements FactoryBean<Ini.Section> {

    //应用方实现此接口从数据源取配置并注入
    @Autowired(required = false)
    private UrlFilterService urlFilterService ;

    @Override
    public Ini.Section getObject() throws Exception {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection(Ini.DEFAULT_SECTION_NAME);
        if(urlFilterService!=null) {
            //通过服务从数据库拉取APP应用的所有URL权限配置信息
            List<UrlFilter> resourceList = urlFilterService.findAll();
            for (UrlFilter resource : resourceList) {
                if (StringUtils.isEmpty(resource.getAuthWay())) {
                    continue;
                }
                if (!StringUtils.isEmpty(resource.getOthers())) {
                    section.put(resource.getUrl(), resource.getOthers());
                    continue;
                }
                if (!StringUtils.isEmpty(resource.getUrl())) {
                    String value = resource.getAuthWay();
                    if (!StringUtils.isEmpty(resource.getRoles())) {
                        value = value + "," + resource.getRoles();
                    }
                    if (!StringUtils.isEmpty(resource.getPermissions())) {
                        value = value + "," + resource.getPermissions();
                    }
                    section.put(resource.getUrl(), value);
                }
            }
            log.info(LogString.initPre+"init UrlFilterSection resourceList="+ resourceList);
        }
        return section;
    }

    @Override
    public Class<?> getObjectType() {
        return Ini.Section.class;
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

}