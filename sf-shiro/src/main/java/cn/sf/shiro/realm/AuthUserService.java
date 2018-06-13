package cn.sf.shiro.realm;

import cn.sf.shiro.domain.SimpleRole;
import cn.sf.shiro.domain.SimpleUser;

import java.util.List;

/**
 * Created by nijianfeng on 17/7/10.
 */
public interface AuthUserService {

    SimpleUser findUser(String username, String pwd);
    List<SimpleRole> getRoles(long userId);

}
