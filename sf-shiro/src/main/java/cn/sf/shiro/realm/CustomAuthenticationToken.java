package cn.sf.shiro.realm;

import lombok.Data;
import lombok.ToString;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Data
@ToString(callSuper = true)
public class CustomAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

     // The username
    private String username;
    // The password
    private String password;

     // 访问凭证
    private Object credentials;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login
     * attempt; default is <code>false</code>
     */
    private boolean rememberMe = false;

    /**
     * The location from where the login attempt occurs, or <code>null</code> if
     * not known or explicitly omitted.
     */
    private String host;


    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

//    public void clear() {
//        this.username = null;
//        this.password = null;
//        this.host = null;
//        this.rememberMe = false;
//    }

}
