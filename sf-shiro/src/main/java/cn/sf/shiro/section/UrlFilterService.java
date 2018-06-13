package cn.sf.shiro.section;

import java.util.List;

/**
 * Created by nijianfeng on 17/7/10.
 */
public interface UrlFilterService {

//    UrlFilter createUrlFilter(UrlFilter urlFilter);
//    UrlFilter updateUrlFilter(UrlFilter urlFilter);
//    void deleteUrlFilter(Long id);
    List<UrlFilter> findAll();
}
