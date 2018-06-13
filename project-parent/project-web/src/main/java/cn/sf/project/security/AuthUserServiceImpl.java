package cn.sf.project.security;

import cn.sf.shiro.domain.SimpleRole;
import cn.sf.shiro.domain.SimpleUser;
import cn.sf.shiro.realm.AuthUserService;
import cn.sf.tools.ldap.LdapHelper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Override
    public SimpleUser findUser(String username, String pwd) {
        boolean isLogIn = LdapHelper.authenticate(username,pwd);
        if(isLogIn){
            SimpleUser simpleUser = new SimpleUser();
            simpleUser.setId(1L);
            simpleUser.setUserName(username);
            simpleUser.setNickName(username);
            return simpleUser;
        }
        return null;
    }

    @Override
    public List<SimpleRole> getRoles(long userId) {
        SimpleRole simpleRole = new SimpleRole();
        simpleRole.setId(1L);
        simpleRole.setName("security");
        simpleRole.setNameDesc("默认角色");
        simpleRole.setPermissions("security:add");
        List<SimpleRole> simpleRoles = Lists.newArrayList();
        simpleRoles.add(simpleRole);
        return simpleRoles;
    }
}
