package cn.sf.tools.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

public class LdapHelper extends LdapEnvInitResult{

    public static void main(String[] args) {

//        System.out.println(addUser("wade","test123456"));
        System.out.println(authenticate("wade","test123456"));
//        System.out.println(updatePwdLdap("wade","test"));
//        System.out.println(authenticate("wade","test"));
//        System.out.println(deleteUsr("wade"));
//        boolean flag = SHAUtil.verifySHA("{SSHA}TZ4LLD1+6vEhgImRg62o9ekiOG84v5Qo","test123456");
//        System.out.println(flag);

    }

    public static boolean addUser(String user, String pwd) {
        if(ctx==null){
            throw new IllegalArgumentException("DirContext is null!!!");
        }
        try {
            BasicAttributes attrsBu = new BasicAttributes();
            BasicAttribute objClassSet = new BasicAttribute("objectclass");
            objClassSet.add("person");
            objClassSet.add("top");
            objClassSet.add("organizationalPerson");
            objClassSet.add("inetOrgPerson");
            attrsBu.put(objClassSet);
            attrsBu.put("sn", user);
            attrsBu.put("uid", user);
            //userPassword的属性来存储用户密码，这一属性是经过SSHA散列的
            attrsBu.put("userPassword", pwd);
            ctx.createSubcontext("cn=" + user + ",ou=People", attrsBu);
            return true;
        } catch (NamingException ex1) {
            ex1.printStackTrace();
        }
        return false;
    }

    public static boolean deleteUsr(String user){
        if(ctx==null){
            throw new IllegalArgumentException("DirContext is null!!!");
        }
        try {
            ctx.destroySubcontext("cn="+user+",ou=People");
            return true;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean authenticate(String user, String pwd) {
        if(ctx==null){
            throw new IllegalArgumentException("DirContext is null!!!");
        }
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            // 查询所有用户
            NamingEnumeration en = ctx.search("", "cn="+user, constraints);
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult sr = (SearchResult) obj;
                    if(sr.getName().equals("cn="+user+",ou=People")){
                        System.out.println("当前登录用户是：" + sr.getName());//当前登录用户是：cn=wade,ou=People
                        Attributes attrs = sr.getAttributes();
                        if (attrs == null) {
                            System.out.println("No attributes");
                        } else {
                            Attribute attr = attrs.get("userPassword");
                            Object o = attr.get();
                            String pwd2 = new String((byte[]) o);
                            return pwd2.equals(pwd);
                        }
                    }
                } else {
                    System.out.println("is not SearchControls.SUBTREE_SCOPE"+obj);
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updatePwdLdap(String user, String pwd) {
        if(ctx==null){
            throw new IllegalArgumentException("DirContext is null!!!");
        }
        try {
            ModificationItem[] modificationItem = new ModificationItem[1];
            modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", pwd));
            ctx.modifyAttributes("cn=" + user+",ou=People", modificationItem);
            return true;
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    
}  