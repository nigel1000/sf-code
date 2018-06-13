package cn.sf.project.controller.security;

import cn.sf.bean.beans.Response;
import cn.sf.shiro.domain.SimpleUser;
import cn.sf.shiro.utils.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by nijianfeng on 17/7/11.
 */
@Controller
@RequestMapping(
        value = "/security",
        method = {RequestMethod.GET,RequestMethod.POST}
)
@Slf4j
public class RoleController {

    @RequestMapping(value = "/perCanGet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @RequiresPermissions(value = "security:add")
    public Response<SimpleUser> perCanGet(){
        return Response.ok(SecurityContextUtils.getUser());
    }

    @RequestMapping(value = "/perCanNotGet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @RequiresPermissions(value = "security:add1")
    public Response<SimpleUser> perCanNotGet(){
        return Response.ok(SecurityContextUtils.getUser());
    }

    @RequestMapping(value = "/roleCanGet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @RequiresRoles(value = "security")
    public Response<SimpleUser> roleCanGet(){
        return Response.ok(SecurityContextUtils.getUser());
    }

    @RequestMapping(value = "/roleCanNotGet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @RequiresRoles(value = "security1")
    public Response<SimpleUser> roleCanNotGet(){
        return Response.ok(SecurityContextUtils.getUser());
    }

    //see ControllerErrorHandler.processShiroError
    @RequestMapping(value = "/errorRole")
    @RequiresRoles(value = "security1")
    public String errorRole(){
        return "pages/welcome";
    }


}
