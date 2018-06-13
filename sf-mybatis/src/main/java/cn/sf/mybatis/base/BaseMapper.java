package cn.sf.mybatis.base;

import cn.sf.bean.beans.page.PageResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 17/7/16.
 */
public interface BaseMapper<T> {

    int create(T item);
    int creates(List<T> items);
    int delete(Long id);
    int deletes(List<Long> ids);
    int update(T item);
    T load(Long id);
    List<T> loads(List<Long> ids);
    List<T> list(Map<String, Object> criteriaMap);
    List<T> list(T item);
    long count(Map<String, Object> criteriaMap);
    long count(T item);
    List<T> paging(Map<String, Object> criteriaMap);
    List<T> paging(T item);
    //分页
    default PageResult<T> paging(Integer offset, Integer limit, Map<String, Object> criteriaMap){
        if(criteriaMap == null) {
            criteriaMap = Maps.newHashMap();
        }
        long total = this.count(criteriaMap);
        if(total <= 0L) {
            return PageResult.empty();
        }
        criteriaMap.put("offset", offset);
        criteriaMap.put("limit", limit);
       return PageResult.gen(total, this.paging(criteriaMap));
    }
    default PageResult<T> paging(Integer offset, Integer limit, T item){
        Map params = Maps.newHashMap();
        if(item != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            mapper.registerModule(new GuavaModule());
            Map map = mapper.convertValue(item, Map.class);
            params.putAll(map);
        }
        long total = this.count(item);
        if(total <= 0L) {
            return PageResult.empty();
        }
        params.put("offset", offset);
        params.put("limit", limit);
        return PageResult.gen(total, this.paging(params));
    }

}
