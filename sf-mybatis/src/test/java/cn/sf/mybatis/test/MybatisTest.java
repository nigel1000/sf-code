package cn.sf.mybatis.test;

import cn.sf.bean.constants.LogString;
import cn.sf.mybatis.base.BaseTest;
import cn.sf.mybatis.test.prepare.Project;
import cn.sf.mybatis.test.prepare.TransactionTest;
import cn.sf.mybatis.test.prepare.dao.ProjectMapper;
import cn.sf.mybatis.test.prepare.dao.ProjectMasterDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 17/6/23.
 */
public class MybatisTest extends BaseTest {

    @Resource
    private TransactionTest transactionTest;
    @Resource
    private ProjectMasterDao projectMasterDao;
    @Autowired
    private ProjectMapper projectMapper;

    @Test
    public void transactionTest(){
        transactionTest.transactional();
    }

    @Test
    public void mapperCreate(){
        Project project = new Project();
        project.setProjectName("mapperSave");
        projectMapper.create(project);
        System.out.println(LogString.initPre+project.getId());
    }
    @Test
    public void mapperCreates(){
        Project p1 = new Project();
        p1.setProjectName("mapperCreates");
        Project p2 = new Project();
        p2.setProjectName("mapperCreates");
        List<Project> pList = Lists.newArrayList();
        pList.add(p1);
        pList.add(p2);
        projectMapper.creates(pList);
        System.out.println(LogString.initPre+pList);
    }
    @Test
    public void mapperDelete(){
        Project project = new Project();
        project.setProjectName("mapperDelete");
        projectMapper.create(project);
        System.out.println(LogString.initPre+project.getId());
        projectMapper.delete(project.getId());
    }
    @Test
    public void mapperDeletes(){
        Project p2 = new Project();
        p2.setProjectName("mapperDeletes");
        Project p1 = new Project();
        p1.setProjectName("mapperDeletes");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        projectMapper.deletes(Lists.newArrayList(p1.getId(),p2.getId()));
    }
    @Test
    public void mapperUpdate(){
        Project project = new Project();
        project.setProjectName("mapperUpdate");
        projectMapper.create(project);
        System.out.println(LogString.initPre+project);
        project.setProjectName("mapperUpdate##");
        projectMapper.update(project);
        System.out.println(LogString.initPre+project);
    }
    @Test
    public void mapperLoad(){
        Project project = new Project();
        project.setProjectName("mapperLoad");
        projectMapper.create(project);
        System.out.println(LogString.initPre+project);
        projectMapper.load(project.getId());
        System.out.println(LogString.initPre+project);
    }

    @Test
    public void mapperLoads(){
        Project p2 = new Project();
        p2.setProjectName("mapperLoads");
        Project p1 = new Project();
        p1.setProjectName("mapperLoads");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        System.out.println(LogString.initPre+projectMapper.loads(Lists.newArrayList(p1.getId(),p2.getId())));
    }

    @Test
    public void mapperListMap(){
        Project p2 = new Project();
        p2.setProjectName("mapperListMap");
        Project p1 = new Project();
        p1.setProjectName("mapperListMap");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        Map<String,Object> map = Maps.newHashMap();
        map.put("projectName","mapperListMap");
        System.out.println(LogString.initPre+projectMapper.list(map));
    }
    @Test
    public void mapperListObject(){
        Project p2 = new Project();
        p2.setProjectName("mapperListObject");
        Project p1 = new Project();
        p1.setProjectName("mapperListObject");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        Project map = new Project();
        map.setProjectName("mapperListObject");
        System.out.println(LogString.initPre+projectMapper.list(map));
    }
    @Test
    public void mapperCountMap(){
        Project p2 = new Project();
        p2.setProjectName("mapperCountMap");
        Project p1 = new Project();
        p1.setProjectName("mapperCountMap");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        Map<String,Object> map = Maps.newHashMap();
        map.put("projectName","mapperCountMap");
        System.out.println(LogString.initPre+projectMapper.count(map));
    }
    @Test
    public void mapperCountObject(){
        Project p2 = new Project();
        p2.setProjectName("mapperCountObject");
        Project p1 = new Project();
        p1.setProjectName("mapperCountObject");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        Project map = new Project();
        map.setProjectName("mapperCountObject");
        System.out.println(LogString.initPre+projectMapper.count(map));
    }
    @Test
    public void mapperPaging(){
        Project p2 = new Project();
        p2.setProjectName("mapperPaging");
        Project p1 = new Project();
        p1.setProjectName("mapperPaging");
        projectMapper.create(p1);
        projectMapper.create(p2);
        System.out.println(LogString.initPre+Lists.newArrayList(p1,p2));
        Map<String,Object> map = Maps.newHashMap();
        map.put("projectName","mapperPaging");
        map.put("offset",0);
        map.put("limit",1);
        System.out.println(LogString.initPre+projectMapper.paging(map));
        Project mapT = new Project();
        mapT.setProjectName("mapperPaging");
        mapT.setOffset(0);
        mapT.setLimit(1);
        System.out.println(LogString.initPre+projectMapper.paging(mapT));

    }
    @Test
    public void mapperPagingMap(){
        Map<String,Object> map = Maps.newHashMap();
        map.put("projectName","mapperPaging");
        System.out.println(LogString.initPre+projectMapper.paging(0,1,map));
    }
    @Test
    public void mapperPagingObject(){
        Project map = new Project();
        map.setProjectName("mapperPaging");
        System.out.println(LogString.initPre+projectMapper.paging(0,1,map));
    }





}
