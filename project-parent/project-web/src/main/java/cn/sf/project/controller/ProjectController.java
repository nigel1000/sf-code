package cn.sf.project.controller;

import cn.sf.auto.aop.print.AutoLog;
import cn.sf.bean.beans.Response;
import cn.sf.bean.beans.page.PageParam;
import cn.sf.bean.beans.page.PageResult;
import cn.sf.bean.utils.BeanCopyUtil;
import cn.sf.compiler.rap.RapClazz;
import cn.sf.compiler.rap.RapMethod;
import cn.sf.compiler.rap.RapParam;
import cn.sf.project.dto.ProjectDto;
import cn.sf.project.param.ProjectReq;
import cn.sf.project.service.ProjectService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 缓存管理
 */
@Controller
@RequestMapping(
		value = "/project",
		method = {RequestMethod.GET,RequestMethod.POST}
)
@Slf4j
@AutoLog
@RapClazz(modulePath = "项目管理")
public class ProjectController {

	@Resource
	private ProjectService projectService;

	//http://127.0.0.1:9091/project/add?id=5&projectName=哈哈哈&projectTypeKey=-1
	@RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@RapMethod(methodMeans = "添加项目")
	public Response<Boolean> add(
			@RapParam(paramMeans = "项目信息") ProjectReq project
	){
		return Response.ok(projectService.save(BeanCopyUtil.genBean(project,ProjectDto.class)));
	}
	//http://127.0.0.1:9091/project/get/5
	@RequestMapping(value = "/get/{id}")
	public String get(
			@PathVariable Long id,
			Model model){
		model.addAttribute("project",BeanCopyUtil.genBean(projectService.getById(id),ProjectReq.class));
		return "pages/project/project";
	}
	//http://127.0.0.1:9091/project/getById?id=5
	@RequestMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Response<ProjectReq> getById(Long id){
		return Response.ok(BeanCopyUtil.genBean(projectService.getById(id),ProjectReq.class));
	}
	//http://127.0.0.1:9091/project/search
	@RequestMapping(value = "/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Response<PageResult<ProjectReq>> search(
			@RequestParam(value = "projectName", required = false) String projectName){
		Map<String,Object> map = Maps.newHashMap();
		if(projectName!=null){
//			map.put("projectName",projectName);
			map.put("projectNameLike",projectName);
		}
		PageResult<ProjectDto> paging = projectService.paging(map, PageParam.valueOfByOffset(0,20));
		return Response.ok(PageResult.gen(paging.getTotal(),BeanCopyUtil.genBeanList(paging.getData(),ProjectReq.class)));
	}


}