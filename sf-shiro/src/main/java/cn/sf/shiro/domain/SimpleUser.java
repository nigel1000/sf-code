package cn.sf.shiro.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleUser implements Serializable {

	// 用户id
	private long id;
	// 用户名账号
	private String userName;
	// 用户名昵称
	private String nickName;
	// 账号是否锁定
	private boolean locked = false;

}