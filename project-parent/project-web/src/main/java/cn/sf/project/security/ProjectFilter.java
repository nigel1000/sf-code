package cn.sf.project.security;

import cn.sf.shiro.filter.BaseSecurityFilter;
import cn.sf.shiro.realm.CustomAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Slf4j
public class ProjectFilter extends BaseSecurityFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        CustomAuthenticationToken token = new CustomAuthenticationToken();
        token.setHost(request.getRemoteHost());
        token.setUsername(request.getParameter("username"));
        token.setPassword(request.getParameter("password"));
        return token;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        if(loginRequest!=null){
            //如果不是登录请求  就不进行登录尝试
            String uri = ((HttpServletRequest)request).getRequestURI();
            if(!uri.contains(loginRequest)){
                return false;
            }
        }
        //没有username和password就不进行登录尝试
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(StringUtils.isBlank(username)){
            return false;
        }
        if(StringUtils.isBlank(password)){
            return false;
        }
        return true;
    }

}
