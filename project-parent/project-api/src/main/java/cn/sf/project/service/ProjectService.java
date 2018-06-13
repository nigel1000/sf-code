package cn.sf.project.service;

import cn.sf.bean.beans.page.PageParam;
import cn.sf.bean.beans.page.PageResult;
import cn.sf.project.dto.ProjectDto;

import java.util.Map;

/**
 * Created by nijianfeng on 17/6/10.
 */
public interface ProjectService {

    Boolean save(ProjectDto projectDto);

    ProjectDto getById(Long id);

    PageResult<ProjectDto> paging(Map<String,Object> criteriaMap, PageParam pageInfo);

}
