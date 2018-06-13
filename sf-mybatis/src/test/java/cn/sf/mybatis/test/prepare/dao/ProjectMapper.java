package cn.sf.mybatis.test.prepare.dao;


import cn.sf.mybatis.base.BaseMapper;
import cn.sf.mybatis.test.prepare.Project;
import org.apache.ibatis.annotations.*;

/**
 * Created by nijianfeng on 17/6/23.
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Insert("insert into project (id, project_name) values (#{project.id}, #{project.projectName})")
    int save(@Param("project") Project project);

}
