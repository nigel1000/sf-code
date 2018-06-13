package cn.sf.project.test.controller;

import cn.sf.project.controller.ProjectController;
import cn.sf.project.dto.ProjectDto;
import cn.sf.project.service.ProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ProjectControllerTests {

    private MockMvc mvc;
//    @Autowired
//    private WebApplicationContext webApplicationContext;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mvc = MockMvcBuilders.standaloneSetup(projectController).build();
        Mockito.when(projectService.getById(Mockito.anyLong())).thenAnswer(new Answer<ProjectDto>() {
            @Override
            public ProjectDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setProjectName("集成测试");
                return projectDto;
            }
        });
    }
    @Test
    public void testGetProject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/project/getById?id=2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"success\":true,\"result\":{\"id\":null,\"projectName\":\"集成测试\",\"projectType\":null,\"projectTypeKey\":null,\"projectTypeDesc\":null},\"error\":null}")));
    }
}