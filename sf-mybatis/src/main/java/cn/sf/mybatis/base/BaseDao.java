package cn.sf.mybatis.base;

import cn.sf.bean.beans.page.PageResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> extends SqlSessionDaoSupport {

//    protected static final String CREATE = "create";
//    protected static final String CREATES = "creates";
//    protected static final String DELETE = "delete";
//    protected static final String DELETES = "deletes";
//    protected static final String UPDATE = "update";
//    protected static final String LOAD = "load";
//    protected static final String LOADS = "loads";
//    protected static final String LIST = "list";
//    protected static final String COUNT = "count";
//    protected static final String PAGING = "paging";

    public final String nameSpace;

    private final static List EMPTYLIST= Lists.newArrayList();

    public BaseDao() {
        this.nameSpace = this.getClass().getName();
//        //等于泛型的全路径名
//        if(this.getClass().getGenericSuperclass() instanceof ParameterizedType) {
//            this.nameSpace = ((Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getName();
//        } else {
//            this.nameSpace = ((Class)((ParameterizedType)this.getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0]).getName();
//        }
    }

    public void init (SqlSessionFactory factory) {
        super.setSqlSessionFactory(factory);
    }

    @Override
    public abstract void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);

//    @Autowired(required = false)
//    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
//        super.setSqlSessionTemplate(sqlSessionTemplate);
//    }

    public Boolean create(T t) {
        return this.getSqlSession().insert(this.sqlId("create"), t)==1;
    }

    public Integer creates(List<T> ts) {
        return this.getSqlSession().insert(this.sqlId("creates"), ts);
    }

    public Integer creates(T t0, T t1, T... tn) {
        return this.getSqlSession().insert(this.sqlId("creates"), Arrays.asList(new Object[]{t0,t1, tn}));
    }

    public Boolean delete(Long id) {
        return this.getSqlSession().delete(this.sqlId("delete"), id) == 1;
    }

    public Integer deletes(List<Long> ids) {
        return this.getSqlSession().delete(this.sqlId("deletes"), ids);
    }

    public Integer deletes(Long id0, Long id1, Long... idn) {
        return this.getSqlSession().delete(this.sqlId("deletes"),Arrays.asList(new Serializable[]{id0,id1, idn}));
    }

    public Boolean update(T t) {
        return this.getSqlSession().update(this.sqlId("update"),t) == 1;
    }

    public T load(Integer id) {
        return this.load(Long.valueOf(id));
    }

    public T load(Long id) {
        return this.getSqlSession().selectOne(this.sqlId("load"), id);
    }

    public List<T> loads(List<Long> ids) {
        return CollectionUtils.isEmpty(ids)? EMPTYLIST:this.getSqlSession().selectList(this.sqlId("loads"), ids);
    }

    public List<T> loads(Long id0, Long id1, Long... idn) {
        return this.getSqlSession().selectList(this.sqlId("loads"), Arrays.asList(id0, id1, idn));
    }

    public List<T> listAll() {
        return this.list((T) null);
    }

    public List<T> list(T t) {
        return this.getSqlSession().selectList(this.sqlId("list"), t);
    }

    public List<T> list(Map<?, ?> criteria) {
        return this.getSqlSession().selectList(this.sqlId("list"), criteria);
    }

    public Long count(Map<String, Object> criteria){
        return this.getSqlSession().selectOne(this.sqlId("count"), criteria);
    }

    public Long count(T t){
        return this.getSqlSession().selectOne(this.sqlId("count"), t);
    }

    public PageResult<T> paging(Integer offset, Integer limit) {
        return this.paging(offset, limit, Maps.newHashMap());
    }

    public PageResult<T> paging(Integer offset, Integer limit, T criteria) {
        HashMap<String,Object> params = Maps.newHashMap();
        if(criteria != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.registerModule(new GuavaModule());
            Map map = mapper.convertValue(criteria, Map.class);
            params.putAll(map);
        }

        Long total = (Long)this.getSqlSession().selectOne(this.sqlId("count"), criteria);
        if(total <= 0L) {
            return PageResult.empty();
        }
        params.put("offset", offset);
        params.put("limit", limit);
        List<T> datas = this.getSqlSession().selectList(this.sqlId("paging"), params);
        return PageResult.gen(total, datas);
    }
    public PageResult<T> paging(Integer offset, Integer limit, Map<String, Object> criteria) {
        if(criteria == null) {
            criteria = Maps.newHashMap();
        }

        Long total = this.getSqlSession().selectOne(this.sqlId("count"), criteria);
        if(total <= 0L) {
            return PageResult.empty();
        } else {
            criteria.put("offset", offset);
            criteria.put("limit", limit);
            List<T> datas = this.getSqlSession().selectList(this.sqlId("paging"), criteria);
            return PageResult.gen(total, datas);
        }
    }
    public PageResult<T> paging(Map<String, Object> criteria) {
        if(criteria == null) {
            criteria = Maps.newHashMap();
        }

        Long total = (Long)this.getSqlSession().selectOne(this.sqlId("count"), criteria);
        if(total <= 0L) {
            return PageResult.empty();
        } else {
            List<T> datas = this.getSqlSession().selectList(this.sqlId("paging"), criteria);
            return PageResult.gen(total, datas);
        }
    }
    
    protected String sqlId(String id) {
        return this.nameSpace + "." + id;
    }

}
