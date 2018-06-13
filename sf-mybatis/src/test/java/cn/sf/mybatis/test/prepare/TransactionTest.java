package cn.sf.mybatis.test.prepare;

import cn.sf.mybatis.test.prepare.dao.ProjectMapper;
import cn.sf.mybatis.test.prepare.dao.ProjectMasterDao;
import cn.sf.mybatis.test.prepare.dao.ProjectSlaveDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nijianfeng on 17/6/23.
 */
@Component
public class TransactionTest {

    @Autowired
    private ProjectMasterDao projectMasterDao;
    @Autowired
    private ProjectSlaveDao projectSlaveDao;
    @Autowired
    private ProjectMapper projectMapper;

    @Transactional(transactionManager = "defaultMasterTransactionManager")
    public void transactional(){
        Project project = new Project();
        project.setProjectName("master多数据源");
        projectMasterDao.create(project);
//        throw new RuntimeException();
        System.out.println("######################");
        project.setProjectName("slave多数据源");
        project.setId(project.getId()+1);
//        projectSlaveDao.create(project);
        projectMapper.save(project);
        System.out.println("######################");
        throw new RuntimeException();
    }

}
