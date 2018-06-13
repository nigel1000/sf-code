package cn.sf.project.security;

import cn.sf.shiro.section.UrlFilter;
import cn.sf.shiro.section.UrlFilterService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Service
public class UrlFilterServiceImpl implements UrlFilterService {

    @Override
    public List<UrlFilter> findAll() {
        UrlFilter urlFilter = new UrlFilter();
        urlFilter.setId(1L);
        urlFilter.setUrl("/security/*");
        urlFilter.setName("安全url");
        urlFilter.setAuthWay("projectFilter");
        urlFilter.setPermissions("perms[security:add]");
        urlFilter.setRoles("roles[security]");
        urlFilter.setOrderBy(0);
        List<UrlFilter> urlFilters = Lists.newArrayList();
        urlFilters.add(urlFilter);
        return urlFilters;
    }
}
