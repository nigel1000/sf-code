package cn.sf.project.controller;

import cn.sf.shiro.utils.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Slf4j
@Controller
public class LoginController {

    @RequestMapping(value = "/")
    public String toLogin(){
        return getLoginPage();
    }
    @RequestMapping(value = "/login")
    public String login(){
        return getLoginPage();
    }
    private String getLoginPage(){
        return "pages/login";
    }

    @RequestMapping(value = "/security/login", method = RequestMethod.POST)
    public String loginIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectURL = ServletRequestUtils.getStringParameter(request, "redirectURL", "");
        if(!StringUtils.isBlank(redirectURL)) {
            WebUtils.issueRedirect(request, response, redirectURL);
        }
        return "pages/welcome";
    }

    @RequestMapping(value = "/security/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityContextUtils.logout();
        WebUtils.issueRedirect(request, response, request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort());
    }

    @RequestMapping(value = "/security/welcome")
    public String welcome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "pages/welcome";
    }



}
