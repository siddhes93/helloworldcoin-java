package com.helloworldcoin.application.interceptor;

import com.helloworldcoin.application.vo.framwork.Response;
import com.helloworldcoin.util.StringsUtil;
import com.helloworldcoin.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
@Component
public class IpInterceptor implements HandlerInterceptor {

	private static final String ALL_IP = "*";

	private static final List<String> DEFAULT_ALLOW_IPS = Arrays.asList("localhost","127.0.0.1","0:0:0:0:0:0:0:1");

	private static final String ALLOW_IPS_KEY = "allowIps";
	private static final String ALLOW_IPS_VALUE_SEPARATOR = ",";

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws IOException {
		String remoteHost = httpServletRequest.getRemoteHost();
		if(isIpAllow(remoteHost)){
			return true;
		}else {
			httpServletResponse.setHeader("Content-type", "application/json;");
			httpServletResponse.setStatus(500);
			httpServletResponse.setCharacterEncoding("UTF-8");
			Response response = Response.serviceUnauthorized();
			String jsonStringResponse = JsonUtil.toString(response);
			httpServletResponse.getWriter().write(jsonStringResponse);
			return false;
		}
	}

	private boolean isIpAllow(String ip){
        if(StringsUtil.contains(DEFAULT_ALLOW_IPS,ip)){
            return true;
        }
		List<String> allowIps = getAllowIps();
		if(StringsUtil.contains(allowIps,ALL_IP)){
			return true;
		}
		if(StringsUtil.contains(allowIps,ip)){
			return true;
		}
		return false;
	}

	private List<String> getAllowIps(){
		String allowIps = System.getProperty(ALLOW_IPS_KEY);
		return StringsUtil.split(allowIps,ALLOW_IPS_VALUE_SEPARATOR);
	}
}