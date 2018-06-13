package cn.sf.tools.ldap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * Created by nijianfeng on 17/6/27.
 */
public class LdapEnvInitResult {

    protected static DirContext ctx;

    static {
        String password = "test123456";    //bindpwd  TZ4LLD1+6vEhgImRg62o9ekiOG84v5Qo
        String root = "dc=0575s,dc=com"; // root
        Hashtable<String,String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://114.55.72.6:389/" + root);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin,"+root);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            // 链接ldap
            ctx = new InitialDirContext(env);
            System.out.println("admin 认证成功");
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("admin 认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("admin 认证出错：");
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(ctx!=null) {
                    ctx.close();
                }
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }));
    }

}
