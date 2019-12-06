package com.tao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tao.interceptor.MyInterceptor;
import com.tao.limiter.LeakBucketLimiter;



@Configuration
public class MvcConfigger implements WebMvcConfigurer{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MyInterceptor()).addPathPatterns("/*").excludePathPatterns("/index");
	}
	@Bean("leakBucketLimiter")
	@Scope("singleton")
	public LeakBucketLimiter leakBucketLimiter() {
		return new LeakBucketLimiter(10);
	}
}
