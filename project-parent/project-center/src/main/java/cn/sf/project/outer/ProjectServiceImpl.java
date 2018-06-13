package cn.sf.project.outer;

import cn.sf.auto.aop.excp.AutoExcp;
import cn.sf.auto.aop.print.AutoLog;
import cn.sf.bean.beans.page.PageParam;
import cn.sf.bean.beans.page.PageResult;
import cn.sf.bean.utils.BeanCopyUtil;
import cn.sf.project.dao.ProjectMapper;
import cn.sf.project.domain.Project;
import cn.sf.project.dto.ProjectDto;
import cn.sf.project.service.ProjectService;
import cn.sf.redis.aops.RedisHash;
import cn.sf.redis.enums.BizRedisEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Service("projectService")
@Slf4j
@AutoLog
@AutoExcp
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private ProjectMapper projectMapper;

    @Override
    @Transactional(transactionManager = "defaultUserTransactionManager")
    public Boolean save(ProjectDto projectDto) {
        int addCount = projectMapper.save(BeanCopyUtil.genBean(projectDto, Project.class));
//        if(true){throw new RuntimeException();}
        return addCount==1;
    }

    @Override
    public ProjectDto getById(Long id) {
        return BeanCopyUtil.genBean(projectMapper.load(id),ProjectDto.class);
    }

    @Override
    @RedisHash(group = BizRedisEnum.DEFAULT)
    public PageResult<ProjectDto> paging(Map<String, Object> criteriaMap, PageParam pageInfo) {
        //参数校验
        if(criteriaMap == null){
            log.warn("ProjectServiceImpl#paging#criteria为空!!");
            return PageResult.empty();
        }
        //参数配置
        if(pageInfo==null){
            pageInfo = PageParam.valueOfByOffset(0,20);
        }
        //业务调用
        PageResult<Project> pagingProject = projectMapper.paging(pageInfo.getOffset(),pageInfo.getLimit(),criteriaMap);
        List<ProjectDto> projectDtos = BeanCopyUtil.genBeanList(pagingProject.getData(), ProjectDto.class);

        return PageResult.gen(pagingProject.getTotal(),projectDtos);
    }
}
