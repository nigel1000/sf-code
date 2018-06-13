package cn.sf.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class CommonConfig {

	@Bean
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}

	// 在默认的异常处理机制上加个性异常机制
	// SimpleMappingExceptionResolver和@ControllerAdvice不能同时存在
//	@Resource
//	public void setHandlerExceptionResolver(HandlerExceptionResolver handlerExceptionResolver){
//		SimpleMappingExceptionResolver simpleMappingExceptionResolver= new SimpleMappingExceptionResolver();
//		simpleMappingExceptionResolver.setDefaultErrorView("error/404");
//		simpleMappingExceptionResolver.setExceptionAttribute("exception");
//		Properties properties = new Properties();
//		properties.setProperty("java.lang.RuntimeException", "error/404");
//		properties.setProperty("org.apache.shiro.authc.UnknownAccountException", "error/404");
//		properties.setProperty("org.apache.shiro.authc.LockedAccountException", "error/404");
//		simpleMappingExceptionResolver.setExceptionMappings(properties);
//
//		List<HandlerExceptionResolver> resolvers = Lists.newArrayList();
//		resolvers.addAll(((HandlerExceptionResolverComposite)handlerExceptionResolver).getExceptionResolvers());
//		resolvers.add(simpleMappingExceptionResolver);
//		((HandlerExceptionResolverComposite) handlerExceptionResolver).setExceptionResolvers(resolvers);
//	}

}