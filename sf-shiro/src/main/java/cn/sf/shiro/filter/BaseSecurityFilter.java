package cn.sf.shiro.filter;

import cn.sf.shiro.realm.CustomAuthenticationToken;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

@Slf4j
public abstract class BaseSecurityFilter extends AccessControlFilter {

    @Setter
    protected String loginRequest = null;
    @Setter
    protected List<String> anonUrls = null;

    // step 1
    // 返回false由 #onAccessDenied()来继续处理
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//        if(isLoginRequest(request, response)){
//            return true;
//        }

        if(!CollectionUtils.isEmpty(anonUrls)){
            for(String anonUrl : anonUrls){
                String uri = ((HttpServletRequest)request).getRequestURI();
                if(uri.matches(anonUrl)){
                    return true;
                }
            }
        }

        if(isLoggedIn(request, response)){
            return true;
        }
        return false;
    }

    // step 2
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false;
        // step 3 是否尝试登录
        if (isLoginAttempt(request, response)) {
            // step 4 createToken登录
            loggedIn = executeLogin(request, response);
        }
        //step 5 登录失败或者不进行登录尝试 重定向到登录页面
        if (!loggedIn) {
            redirectToLogin(request, response);
        }
        return loggedIn;
    }

    // 创建用户访问凭证.
    protected abstract AuthenticationToken createToken(ServletRequest request, ServletResponse response);
    // 是否登录
    protected  boolean isLoggedIn(ServletRequest request, ServletResponse response){
        Subject currentUser = getSubject(request, response);
        if(currentUser==null){
            return false;
        }
        if (!currentUser.isAuthenticated()) {
            return false;
        }
        if(loginRequest!=null){
            //如果是登录url则先退出再登录
            String uri = ((HttpServletRequest)request).getRequestURI();
            if(uri.contains(loginRequest)){
                currentUser.logout();
                return false;
            }
        }

        return true;
    }
    // 是否进行登录尝试
    protected abstract boolean isLoginAttempt(ServletRequest request, ServletResponse response);



    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        // 1.获取用户访问凭证
        CustomAuthenticationToken token = (CustomAuthenticationToken) createToken(request, response);
        if (token == null)
            return false;
        if (!StringUtils.hasText(token.getUsername()))
            return false;

        // 2.提交凭证
        Subject subject = getSubject(request, response);
        subject.login(token);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        // 1.保存请求信息.
        saveRequest(request);
        // 2.登录url加重定向url
        String successUrl = null;
        SavedRequest savedRequest = WebUtils.getSavedRequest(request);
        if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(AccessControlFilter.GET_METHOD)) {
            successUrl = savedRequest.getRequestUrl();
        }
        if (successUrl == null) {
            successUrl =  "/";
        }
        StringBuilder loginUrl = new StringBuilder(getLoginUrl());
        loginUrl.append("?redirectURL=");
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
        if (!StringUtils.startsWithIgnoreCase(successUrl, basePath)) {
            successUrl = basePath + successUrl;
        }
        loginUrl.append(URLEncoder.encode(successUrl, "UTF-8"));
        // 3.清除保存的请求信息
        clearSavedRequest(request);
        // 4.清除认证信息
        cleanupAuthenticationInfo(request, response);
        // 5.跳转到登录页
        WebUtils.issueRedirect(request, response, loginUrl.toString());
    }

    // 清除保存的请求信息
    // 如果不在约定的url列表里则清除
    private Collection<String> savedRequestUrls;
    private void clearSavedRequest(ServletRequest request) {
        if (CollectionUtils.isEmpty(savedRequestUrls)) {
            WebUtils.getAndClearSavedRequest(request);
            return;
        }

        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String requestURI = httpRequest.getRequestURI();
        for (String savedRequestUrl : savedRequestUrls) {
            if (requestURI.contains(savedRequestUrl)) {
                return;
            }
        }

        WebUtils.getAndClearSavedRequest(request);
    }

    /**
     * 清除认证信息.
     *
     * @param request
     */
    private void cleanupAuthenticationInfo(ServletRequest request, ServletResponse response) {
        // 清除cookie等工作
//        final HttpServletResponse httpServletResponse = WebUtils.toHttp(response);

    }

}