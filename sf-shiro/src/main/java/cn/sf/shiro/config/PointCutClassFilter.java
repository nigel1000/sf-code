package cn.sf.shiro.config;

import org.springframework.aop.ClassFilter;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

class PointCutClassFilter implements ClassFilter, Serializable {

	private List<String> packages;

	public PointCutClassFilter(List<String> packages) {
		this.packages = packages;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		if(clazz.isInterface()){
			return false;
		}
		if(CollectionUtils.isEmpty(packages)){
			return false;
		}
		for(String classPackage : packages){
			return clazz.getName().startsWith(classPackage);
		}
		return true;
	}


}