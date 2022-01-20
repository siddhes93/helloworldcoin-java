package com.helloworldcoin.application.configuration;

import com.helloworldcoin.application.vo.framwork.Response;
import com.helloworldcoin.application.interceptor.IpInterceptor;
import com.helloworldcoin.util.JsonUtil;
import com.helloworldcoin.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 *
 * @author x.king xdotking@gmail.com
 */
@Configuration
public class WebMvcConfigurerConfiguration implements WebMvcConfigurer {

	@Autowired
	private IpInterceptor ipInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(ipInterceptor).addPathPatterns("/**");
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> handlerExceptionResolvers) {
		handlerExceptionResolvers.add((httpServletRequest, httpServletResponse, handler, exception) -> {
			LogUtil.error("全局异常拦截。",exception);
			try {
				httpServletResponse.setHeader("Content-type", "application/json;");
				httpServletResponse.setStatus(500);
				httpServletResponse.setCharacterEncoding("UTF-8");
				Response response = Response.serviceUnavailable();
				String jsonStringResponse = JsonUtil.toString(response);
				httpServletResponse.getWriter().write(jsonStringResponse);
			} catch (Exception e) {
				LogUtil.error("将拦截到的全局异常写入到HttpServletResponse出现错误。",e);
			}
			return new ModelAndView();
		});
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowCredentials(true)
				.allowedMethods("*")
				.maxAge(3600);
	}
}