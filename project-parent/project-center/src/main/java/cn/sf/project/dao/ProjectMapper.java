package cn.sf.project.dao;


import cn.sf.mybatis.base.BaseMapper;
import cn.sf.project.domain.Project;
import org.apache.ibatis.annotations.*;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Insert("insert into project (project_name) values (#{project.projectName})")
    int save(@Param("project") Project project);

    @Select("select * from project where id = #{id}")
    @ResultMap("ProjectMap")
    Project getById(@Param("id") Long id);

}
