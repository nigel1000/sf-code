package cn.sf.shiro.utils;

import cn.sf.shiro.domain.SimpleUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public final class SecurityContextUtils {

	private SecurityContextUtils() {}

	// 获取当前用户id
	public static long getUserId() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null)
			return -1L;

		if (currentSubject.getPrincipal() == null)
			return -1L;

		SimpleUser user = (SimpleUser) currentSubject.getPrincipal();
		return user.getId();
	}

	// 获取当前用户名
	public static String getUserName() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null)
			return null;

		if (currentSubject.getPrincipal() == null)
			return null;

		SimpleUser user = (SimpleUser) currentSubject.getPrincipal();
		return user.getUserName();
	}

	// 获取当前用户昵称
	public static String getNickName() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null)
			return null;

		if (currentSubject.getPrincipal() == null)
			return null;

		SimpleUser user = (SimpleUser) currentSubject.getPrincipal();
		return user.getNickName();
	}

	// 退出shiro登录
	public static void logout() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null)
			return;
		if (currentSubject.getPrincipal() == null)
			return;
		currentSubject.logout();
	}

	// 是否已登录
	public static boolean isLogin() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null) {
			return false;
		}
		if (currentSubject.getPrincipal() == null) {
			return false;
		}

		return true;
	}

	// 获取当前用户
	public static SimpleUser getUser() {
		Subject currentSubject = SecurityUtils.getSubject();
		if (currentSubject == null) {
			return null;
		}
		if (currentSubject.getPrincipal() == null) {
			return null;
		}
		return (SimpleUser) currentSubject.getPrincipal();
	}

}