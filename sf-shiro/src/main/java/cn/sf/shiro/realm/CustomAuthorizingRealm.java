package cn.sf.shiro.realm;

import cn.sf.shiro.domain.SimpleRole;
import cn.sf.shiro.domain.SimpleUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class CustomAuthorizingRealm extends AuthorizingRealm {

    @Autowired(required = false)
    private AuthUserService authUserService;

    public CustomAuthorizingRealm() {
        // 设置无需凭证
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        // 缓存角色和权限的信息  修改角色和权限后必须重新登录才能生效
        setCachingEnabled(true);
        // 设置CustomAuthenticationToken
        setAuthenticationTokenClass(CustomAuthenticationToken.class);
        //设置角色和权限的缓存
        setCacheManager(new MemoryConstrainedCacheManager());
    }

     // 执行subject.login(token)时认证回调函数
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        if(authUserService==null){
            throw new IllegalArgumentException("authUserService 未被实现!!");
        }

        CustomAuthenticationToken token =  (CustomAuthenticationToken)authToken;
        String userName = token.getUsername();
        // 1.用户名为空,未通过URS认证
        if (!StringUtils.hasText(userName)) {
            throw new UnknownAccountException("userName is null");
        }

        // 2.根据用户名密码获取用户信息
        SimpleUser simpleUser = authUserService.findUser(userName,token.getPassword());
        if (simpleUser == null) {
            throw new UnknownAccountException(userName + " Not Found");
        }
        if (simpleUser.isLocked()) {
            throw new LockedAccountException("The account for username " + userName
                    + " is locked. Please contact your administrator to unlock it.");
        }

        return new SimpleAuthenticationInfo(simpleUser, token.getCredentials(), getName());
    }
    
    // 授权查询回调函数, 进行鉴权当缓存中无用户的授权信息时调用.
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 1.根据用户名获取用户角色和权限信息
        SimpleUser simpleUser = (SimpleUser)principals.getPrimaryPrincipal();
        List<SimpleRole> list = authUserService.getRoles(simpleUser.getId());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (list != null && !list.isEmpty()) {
            for (SimpleRole role : list) {
                // 基于Role的权限信息teacher，student，boss
                info.addRole(role.getName());
                // 基于Permission的权限信息
                info.addStringPermissions(role.getPermissionList());

            }
        }
        return info;

    }

}