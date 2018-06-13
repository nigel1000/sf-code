package cn.sf.shiro.section;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class UrlFilter implements Serializable {
    //主键id
    private long id;
    //访问路径定义  ／find/user/*
    private String url;
    //url名称描述
    private String name;
    //反问此url的认证方式   anon logout authc
    //http://blog.csdn.net/jspamd/article/details/51181091
    private String authWay;
    //访问此url所需的权限   perms[admin:edit]
    private String permissions;
    //访问此url所对应的角色  roles[admin]
    private String roles;
    //访问此url所对应的其他规则,如果此项不为空即为authWay＝others 譬如:/admin/** = authc,roles[admin],port[8081],rest[user]
    private String others;
    //排序
    private int orderBy;

}