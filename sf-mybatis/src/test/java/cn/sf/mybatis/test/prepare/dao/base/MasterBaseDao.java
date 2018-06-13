package cn.sf.mybatis.test.prepare.dao.base;

import cn.sf.mybatis.base.BaseDao;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 17/6/23.
 */
public class MasterBaseDao<T> extends BaseDao<T> {

    @Resource(name="defaultMasterSqlSessionFactory")
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.init(sqlSessionFactory);
    }
}
