package cn.sf.auto.mybatis;

import cn.sf.auto.mybatis.core.DB2Domain;
import cn.sf.auto.mybatis.core.DB2Dto;
import cn.sf.auto.mybatis.core.DB2Mapper;
import cn.sf.auto.mybatis.util.base.DBUtils;
import cn.sf.auto.util.PropertiesLoad;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class Run {

    public static void main(String[] args) {
        PropertiesLoad.init("mapper.properties");
        // DB2Domain
        DB2Domain db2Domain = new DB2Domain();
        db2Domain.genDomain();
        // DB2Dto
        DB2Dto db2Dto = new DB2Dto();
        db2Dto.genDto();
        // DB2Mapper
        DB2Mapper db2Mapper = new DB2Mapper();
        db2Mapper.genMapper();

        DBUtils.closeConn();
    }
}
