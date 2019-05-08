package com.example.demo;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import workshop.LoggingContext;

import java.util.UUID;

@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("PRE HANDLE/Note not logging id yet");

		String loggingId = request.getHeader(LoggingContext.LOGGING_HEADER_NAME);
		if (loggingId == null) {
			loggingId = UUID.randomUUID().toString();
		}
		MDC.put(LoggingContext.LOGGING_ID_KEY, loggingId);

		//Adding a known MDC value to dynamically adjust logging based on headers
		String dynamicLevel = request.getHeader("X-DEBUGLEVEL");
		if (dynamicLevel != null) {
			MDC.put("dynamicLevel", dynamicLevel.toLowerCase());
		}


		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		log.info("Setting outbound header/Note there is now a logging id");
		response.setHeader(LoggingContext.LOGGING_HEADER_NAME, MDC.get(LoggingContext.LOGGING_ID_KEY));
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {


	}
}