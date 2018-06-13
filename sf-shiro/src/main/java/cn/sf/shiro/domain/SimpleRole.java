package cn.sf.shiro.domain;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class SimpleRole implements Serializable {

	private static final long serialVersionUID = 4361604447715079602L;

	private Long id;
	// role name for example student
	private String name;
	// role desc for example 学生角色
	private String nameDesc;
	// 权限
    private String permissions;

	public List<String> getPermissionList() {
		if (StringUtils.hasText(permissions))
			return Arrays.asList(permissions.split(","));
		return Lists.newArrayList();
	}

}